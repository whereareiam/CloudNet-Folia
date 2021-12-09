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

package eu.cloudnetservice.cloudnet.modules.labymod.config;

import de.dytanic.cloudnet.driver.service.ServiceInfoSnapshot;
import de.dytanic.cloudnet.ext.bridge.BridgeServiceHelper;
import java.util.function.Function;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LabyModServiceDisplay {

  protected final boolean enabled;
  protected final String format;

  public LabyModServiceDisplay(boolean enabled, @Nullable String format) {
    this.enabled = enabled;
    this.format = format;
  }

  public boolean isEnabled() {
    return this.enabled;
  }

  public @Nullable String getDisplay(@Nullable ServiceInfoSnapshot serviceInfoSnapshot) {
    if (serviceInfoSnapshot == null || this.format == null || !this.enabled) {
      return null;
    }
    return BridgeServiceHelper.fillCommonPlaceholders(this.format, null, serviceInfoSnapshot);
  }

  public @Nullable String getFormat() {
    return this.format;
  }
}