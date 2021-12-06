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

package eu.cloudnetservice.cloudnet.ext.signs.node.util;

import de.dytanic.cloudnet.common.io.FileUtils;
import de.dytanic.cloudnet.driver.service.ServiceEnvironmentType;
import de.dytanic.cloudnet.driver.util.DefaultModuleHelper;
import de.dytanic.cloudnet.service.ICloudService;
import eu.cloudnetservice.cloudnet.ext.signs.configuration.SignConfigurationEntry;
import eu.cloudnetservice.cloudnet.ext.signs.configuration.SignsConfiguration;
import java.nio.file.Path;
import java.util.Collection;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;

@Internal
public final class SignPluginInclusion {

  private SignPluginInclusion() {
    throw new UnsupportedOperationException();
  }

  public static void includePluginTo(@NotNull ICloudService cloudService, @NotNull SignsConfiguration configuration) {
    ServiceEnvironmentType type = cloudService.getServiceConfiguration().getServiceId().getEnvironment();
    if (ServiceEnvironmentType.isMinecraftServer(type)
      && hasConfigurationEntry(cloudService.getServiceConfiguration().getGroups(), configuration)) {
      Path pluginDirectory = cloudService.getDirectory().resolve("plugins");
      FileUtils.createDirectory(pluginDirectory);

      Path pluginFile = pluginDirectory.resolve("cloudnet-signs.jar");
      FileUtils.delete(pluginFile);

      if (DefaultModuleHelper.copyCurrentModuleInstanceFromClass(SignPluginInclusion.class, pluginFile)) {
        DefaultModuleHelper.copyPluginConfigurationFileForEnvironment(SignPluginInclusion.class, type, pluginFile);
      }
    }
  }

  public static boolean hasConfigurationEntry(@NotNull Collection<String> groups, @NotNull SignsConfiguration config) {
    for (SignConfigurationEntry entry : config.getConfigurationEntries()) {
      if (groups.contains(entry.getTargetGroup())) {
        return true;
      }
    }
    return false;
  }
}
