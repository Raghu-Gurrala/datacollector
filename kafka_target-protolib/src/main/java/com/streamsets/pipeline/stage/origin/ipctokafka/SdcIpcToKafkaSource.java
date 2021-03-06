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
package com.streamsets.pipeline.stage.origin.ipctokafka;

import com.streamsets.pipeline.api.BatchMaker;
import com.streamsets.pipeline.api.OffsetCommitter;
import com.streamsets.pipeline.api.StageException;
import com.streamsets.pipeline.api.base.BaseSource;
import com.streamsets.pipeline.config.DataFormat;
import com.streamsets.pipeline.stage.destination.kafka.KafkaTargetConfig;
import com.streamsets.pipeline.stage.destination.lib.DataGeneratorFormatConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class SdcIpcToKafkaSource extends BaseSource implements OffsetCommitter {
  private static final Logger LOG = LoggerFactory.getLogger(SdcIpcToKafkaSource.class);

  private final RpcConfigs configs;
  private final KafkaTargetConfig KafkaTargetConfig;
  private final int kafkaMaxMessageSize;
  private IpcToKafkaServer ipcToKafkaServer;
  private long counter;
  private BlockingQueue<Exception> errorQueue;
  private List<Exception> errorList;

  public SdcIpcToKafkaSource(RpcConfigs configs, KafkaTargetConfig KafkaTargetConfig, int kafkaMaxMessageSize) {
    this.configs = configs;
    this.KafkaTargetConfig = KafkaTargetConfig;
    this.kafkaMaxMessageSize = kafkaMaxMessageSize;

    // Although the following is not used it helps validate Kafka connection
    // set the data format to SDC_JSON
    KafkaTargetConfig.dataFormat = DataFormat.SDC_JSON;
    KafkaTargetConfig.dataGeneratorFormatConfig = new DataGeneratorFormatConfig();
  }

  @Override
  protected List<ConfigIssue> init() {
    List<ConfigIssue> issues = super.init();
    issues.addAll(configs.init(getContext()));
    // init and validate kafka configs
    KafkaTargetConfig.init(getContext(), issues);
    if (issues.isEmpty()) {
      errorQueue = new ArrayBlockingQueue<>(100);
      errorList = new ArrayList<>(100);
      ipcToKafkaServer = new IpcToKafkaServer(getContext(), configs, KafkaTargetConfig, kafkaMaxMessageSize, errorQueue);
      try {
        ipcToKafkaServer.start();
      } catch (Exception ex) {
        ConfigIssue issue = getContext().createConfigIssue(null, null, Errors.IPC_KAKFA_ORIG_20, ex.toString());
        LOG.warn(issue.toString(), ex);
        issues.add(issue);
      }
    }
    return issues;
  }

  @Override
  public void destroy() {
    if (ipcToKafkaServer != null) {
      ipcToKafkaServer.stop();
    }
    super.destroy();
  }

  @Override
  public String produce(String lastSourceOffset, int maxBatchSize, BatchMaker batchMaker) throws StageException {
    try {
      Thread.sleep(1000);
    } catch (InterruptedException ex) {
    }
    // report any Kafka producer errors captured by the IpcToKafkaServer's servlet
    errorList.clear();
    errorQueue.drainTo(errorList);
    for (Exception exception : errorList) {
      getContext().reportError(exception);
    }
    return "::asyncipc::" + (counter++) + System.currentTimeMillis();
  }

  @Override
  public void commit(String offset) throws StageException {
  }

}
