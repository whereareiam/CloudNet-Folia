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

package eu.cloudnetservice.cloudnet.ext.report.config;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Collections;
import org.jetbrains.annotations.NotNull;

public class ReportConfiguration {

  public static final ReportConfiguration DEFAULT = new ReportConfiguration(
    true,
    Paths.get("records"),
    5000L,
    "yyyy-MM-dd",
    Collections.singletonList(new PasteService("default", "https://just-paste.it"))
  );

  private final boolean saveRecords;
  private final Path recordDestination;
  private final long serviceLifetime;
  private final String dateFormat;
  private final transient SimpleDateFormat parsedFormat;
  private final Collection<PasteService> pasteServers;

  public ReportConfiguration(
    boolean saveRecords,
    @NotNull Path recordDestination,
    long serviceLifetime,
    @NotNull String dateFormat,
    @NotNull Collection<PasteService> pasteServers
  ) {
    this.saveRecords = saveRecords;
    this.recordDestination = recordDestination;
    this.serviceLifetime = serviceLifetime;
    this.dateFormat = dateFormat;
    this.parsedFormat = new SimpleDateFormat(dateFormat);
    this.pasteServers = pasteServers;
  }

  public boolean isSaveRecords() {
    return this.saveRecords;
  }

  public @NotNull Path getRecordDestination() {
    return this.recordDestination;
  }

  public long getServiceLifetime() {
    return this.serviceLifetime;
  }

  public @NotNull DateFormat getDateFormat() {
    return this.parsedFormat;
  }

  public @NotNull Collection<PasteService> getPasteServers() {
    return this.pasteServers;
  }
}