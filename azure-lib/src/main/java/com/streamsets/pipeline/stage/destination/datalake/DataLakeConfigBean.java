/**
 * Copyright 2016 StreamSets Inc.
 *
 * Licensed under the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.streamsets.pipeline.stage.destination.datalake;

import com.streamsets.pipeline.api.ConfigDef;
import com.streamsets.pipeline.api.ConfigDefBean;
import com.streamsets.pipeline.api.Stage;
import com.streamsets.pipeline.api.ValueChooserModel;
import com.streamsets.pipeline.api.el.SdcEL;
import com.streamsets.pipeline.config.DataFormat;
import com.streamsets.pipeline.config.TimeZoneChooserValues;
import com.streamsets.pipeline.lib.el.RecordEL;
import com.streamsets.pipeline.lib.el.TimeEL;
import com.streamsets.pipeline.lib.el.TimeNowEL;
import com.streamsets.pipeline.stage.destination.lib.DataGeneratorFormatConfig;

import java.util.List;
import java.util.Date;


public class DataLakeConfigBean {
  public static final String ADLS_CONFIG_BEAN_PREFIX = "dataLakeConfig.";
  private static final String ADLS_DATA_FORMAT_CONFIG_PREFIX = ADLS_CONFIG_BEAN_PREFIX + "dataFormatConfig";

  @ConfigDef(
      required = true,
      type = ConfigDef.Type.STRING,
      evaluation = ConfigDef.Evaluation.EXPLICIT,
      defaultValue = "Hash Key",
      label = "Client ID",
      description = "Azure Client ID.",
      displayPosition = 10,
      group = "#0"
  )
  public String clientId;

  @ConfigDef(
      required = true,
      type = ConfigDef.Type.STRING,
      evaluation = ConfigDef.Evaluation.EXPLICIT,
      defaultValue = "https://login.microsoftonline.com/example-example",
      label = "Auth Token Endpoint",
      description = "Azure Auth Token Endpoint.",
      displayPosition = 20,
      group = "#0"
  )
  public String authTokenEndpoint;

  @ConfigDef(
      required = true,
      type = ConfigDef.Type.STRING,
      evaluation = ConfigDef.Evaluation.EXPLICIT,
      defaultValue = "example.azuredatalakestore.net",
      label = "Account FQDN",
      description = "full account FQDN, not just the account name.",
      displayPosition = 30,
      group = "#0"
  )
  public String accountFQDN;

  @ConfigDef(
      required = true,
      type = ConfigDef.Type.STRING,
      evaluation = ConfigDef.Evaluation.EXPLICIT,
      defaultValue = "Hash Key",
      label = "Client Key",
      description = "Azure Client Key.",
      displayPosition = 30,
      group = "#0"
  )
  public String clientKey;

  @ConfigDef(
      required = true,
      type = ConfigDef.Type.STRING,
      elDefs = {RecordEL.class, TimeEL.class, TimeNowEL.class},
      evaluation = ConfigDef.Evaluation.EXPLICIT,
      defaultValue = "",
      label = "Files Name",
      displayPosition = 40,
      group = "#0"
  )
  public String fileNameTemplate;

  @ConfigDef(
      required = true,
      type = ConfigDef.Type.STRING,
      elDefs = {RecordEL.class, TimeEL.class, TimeNowEL.class},
      evaluation = ConfigDef.Evaluation.EXPLICIT,
      defaultValue = "/tmp/out/${YYYY()}-${MM()}-${DD()}-${hh()}",
      label = "Directory Template",
      //description = "Azure Client Key.",
      displayPosition = 50,
      group = "#0"
  )
  public String dirPathTemplate;

  @ConfigDef(
      required = true,
      type = ConfigDef.Type.MODEL,
      defaultValue = "UTC",
      label = "Data Time Zone",
      description = "Time zone to use to resolve the date time of a time-based partition prefix",
      displayPosition = 60,
      group = "#0"
  )
  @ValueChooserModel(TimeZoneChooserValues.class)
  public String timeZoneID;

  @ConfigDef(
      required = true,
      type = ConfigDef.Type.STRING,
      defaultValue = "${time:now()}",
      label = "Time Basis",
      description = "Time basis to use for a record. Enter an expression that evaluates to a datetime. To use the " +
          "processing time, enter ${time:now()}. To use field values, use '${record:value(\"<filepath>\")}'.",
      displayPosition = 70,
      group = "#0",
      elDefs = {RecordEL.class, TimeEL.class, TimeNowEL.class},
      evaluation = ConfigDef.Evaluation.EXPLICIT
  )
  public String timeDriver;

  @ConfigDef(
      required = true,
      type = ConfigDef.Type.MODEL,
      defaultValue = "JSON",
      label = "Data Format",
      description = "Data format to use when writing records to Azure Data Lake Store",
      displayPosition = 100,
      group = "DATA_FORMAT"
  )
  @ValueChooserModel(DataFormatChooserValues.class)
  public DataFormat dataFormat;

  @ConfigDefBean(groups = {"DATALAKE"})
  public DataGeneratorFormatConfig dataFormatConfig = new DataGeneratorFormatConfig();

  public void init(Stage.Context context, List<Stage.ConfigIssue> issues) {
    dataFormatConfig.init(
        context,
        dataFormat,
        Groups.DATALAKE.name(),
        ADLS_DATA_FORMAT_CONFIG_PREFIX,
        issues
    );

  }
}
