/*
 * Copyright 2019-2021 CloudNetService team & contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.dytanic.cloudnet.setup;

import static de.dytanic.cloudnet.console.animation.setup.answer.Parsers.ranged;
import static de.dytanic.cloudnet.console.animation.setup.answer.Parsers.validatedHostAndPort;

import com.google.common.collect.ImmutableSet;
import de.dytanic.cloudnet.CloudNet;
import de.dytanic.cloudnet.config.IConfiguration;
import de.dytanic.cloudnet.console.animation.setup.ConsoleSetupAnimation;
import de.dytanic.cloudnet.console.animation.setup.answer.Parsers.ParserException;
import de.dytanic.cloudnet.console.animation.setup.answer.QuestionAnswerType;
import de.dytanic.cloudnet.console.animation.setup.answer.QuestionListEntry;
import de.dytanic.cloudnet.driver.network.HostAndPort;
import de.dytanic.cloudnet.driver.network.cluster.NetworkClusterNode;
import de.dytanic.cloudnet.driver.service.ProcessSnapshot;
import de.dytanic.cloudnet.util.NetworkAddressUtil;
import java.util.Collection;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;

public class DefaultConfigSetup extends DefaultClusterSetup {

  private static final Collection<String> DEFAULT_WHITELIST = ImmutableSet.<String>builder()
    .add("127.0.0.1")
    .add("127.0.1.1")
    .addAll(NetworkAddressUtil.getAvailableIpAddresses())
    .build();

  @Override
  public void applyQuestions(@NotNull ConsoleSetupAnimation animation) {
    // pre-save all available ip addresses
    Collection<String> addresses = NetworkAddressUtil.getAvailableIpAddresses();
    // apply the questions
    animation.addEntries(
      // eula agreement
      QuestionListEntry.<Boolean>builder()
        .key("eula")
        .translatedQuestion("cloudnet-init-eula")
        .answerType(QuestionAnswerType.<Boolean>builder()
          .recommendation("yes")
          .possibleResults("yes")
          .translatedInvalidInputMessage("cloudnet-init-eula-not-accepted")
          .parser(input -> {
            // we can only proceed if the user accepts the eula
            if (input.equalsIgnoreCase("yes")) {
              return true;
            } else {
              throw ParserException.INSTANCE;
            }
          }))
        .build(),
      // network server host
      QuestionListEntry.<HostAndPort>builder()
        .key("internalHost")
        .translatedQuestion("cloudnet-init-setup-internal-host")
        .answerType(QuestionAnswerType.<HostAndPort>builder()
          .recommendation(NetworkAddressUtil.getLocalAddress() + ":1410")
          .possibleResults(addresses.stream().map(addr -> addr + ":1410").collect(Collectors.toList()))
          .parser(validatedHostAndPort(true)))
        .build(),
      // web server host
      QuestionListEntry.<HostAndPort>builder()
        .key("webHost")
        .translatedQuestion("cloudnet-init-setup-web-host")
        .answerType(QuestionAnswerType.<HostAndPort>builder()
          .recommendation(NetworkAddressUtil.getLocalAddress() + ":2812")
          .possibleResults(addresses.stream().map(addr -> addr + ":2812").collect(Collectors.toList()))
          .parser(validatedHostAndPort(true)))
        .build(),
      // service bind host address
      QuestionListEntry.<HostAndPort>builder()
        .key("hostAddress")
        .translatedQuestion("cloudnet-init-setup-host-address")
        .answerType(QuestionAnswerType.<HostAndPort>builder()
          .possibleResults(addresses)
          .recommendation(NetworkAddressUtil.getLocalAddress())
          .parser(validatedHostAndPort(false)))
        .build(),
      // maximum memory usage
      QuestionListEntry.<Integer>builder()
        .key("memory")
        .translatedQuestion("cloudnet-init-setup-memory")
        .answerType(QuestionAnswerType.<Integer>builder()
          .parser(ranged(128, Integer.MAX_VALUE))
          .possibleResults("128", "512", "1024", "2048", "4096", "8192", "16384", "32768", "65536")
          .recommendation((int) ((ProcessSnapshot.OS_BEAN.getTotalPhysicalMemorySize() / (1024 * 1024)) - 512)))
        .build()
    );
    // apply the cluster setup questions
    super.applyQuestions(animation);
  }

  @Override
  public void handleResults(@NotNull ConsoleSetupAnimation animation) {
    IConfiguration config = CloudNet.getInstance().getConfig();
    // init the local node identity
    HostAndPort host = animation.getResult("internalHost");
    config.setIdentity(new NetworkClusterNode(
      animation.hasResult("nodeId") ? animation.getResult("nodeId") : "Node-1",
      new HostAndPort[]{host}));
    // whitelist the host address
    config.getIpWhitelist().add(host.getHost());

    // init the host address
    config.setHostAddress(animation.getResult("hostAddress"));
    config.setConnectHostAddress(animation.getResult("hostAddress"));

    // init the web host address
    config.getHttpListeners().add(animation.getResult("webHost"));

    // set the maximum memory
    config.setMaxMemory(animation.getResult("memory"));

    // whitelist all default addresses and finish by saving the config
    config.getIpWhitelist().addAll(DEFAULT_WHITELIST);
    config.save();

    // apply animation results of the cluster setup
    super.handleResults(animation);
  }
}