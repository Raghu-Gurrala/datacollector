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
package com.streamsets.pipeline.stage.lib.hive;

import com.streamsets.pipeline.api.Field;
import com.streamsets.pipeline.api.Record;
import com.streamsets.pipeline.api.StageException;
import com.streamsets.pipeline.sdk.RecordCreator;
import com.streamsets.pipeline.stage.lib.hive.typesupport.HiveType;
import com.streamsets.pipeline.stage.lib.hive.typesupport.HiveTypeConfig;
import com.streamsets.pipeline.stage.lib.hive.typesupport.HiveTypeInfo;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Date;
import java.util.*;

public class TestHiveMetastoreUtil {

  // Utility function to generate HiveTypeInfo from HiveType.
  public static HiveTypeInfo generatePrimitiveTypeInfo(HiveType type){
    HiveTypeConfig config = new HiveTypeConfig();
    config.valueType = type;
    type.getSupport().createTypeInfo(config);
    return type.getSupport().createTypeInfo(config);
  }

  // Utility function to generate HiveTypeInfo from HiveType.
  public static HiveTypeInfo generateDecimalTypeInfo(int precision, int scale){
    HiveTypeConfig config = new HiveTypeConfig();
    config.valueType = HiveType.DECIMAL;
    config.precision = precision;
    config.scale = scale;
    return HiveType.DECIMAL.getSupport().createTypeInfo(config);
  }

  @Test
  public void testConvertRecordToHMSTypeSimple() {
    // Test for types that don't require type conversion
    Record record = RecordCreator.create();
    Map<String, Field> map = new HashMap<>();
    map.put("string", Field.create("2016-05-27"));
    map.put("boolean", Field.create(true));
    map.put("integer", Field.create(12345));
    map.put("negative", Field.create(-12345));
    map.put("long", Field.create(100000L));
    map.put("float", Field.create(12.5f));
    map.put("double", Field.create(1232345891.4d));
    record.set(Field.create(map));

    Map<String, HiveTypeInfo> expected = new LinkedHashMap<>();
    expected.put("string", generatePrimitiveTypeInfo(HiveType.STRING));
    expected.put("boolean", generatePrimitiveTypeInfo(HiveType.BOOLEAN));
    expected.put("integer", generatePrimitiveTypeInfo(HiveType.INT));
    expected.put("negative", generatePrimitiveTypeInfo(HiveType.INT));
    expected.put("long", generatePrimitiveTypeInfo(HiveType.BIGINT));
    expected.put("float", generatePrimitiveTypeInfo(HiveType.FLOAT));
    expected.put("double", generatePrimitiveTypeInfo(HiveType.DOUBLE));

    Map<String, HiveTypeInfo> actual = null;
    try {
      actual = HiveMetastoreUtil.convertRecordToHMSType(record);
    } catch (StageException e){
      Assert.fail("convertRecordToHMSType threw StageException:" + e.getMessage());
    }
    Assert.assertNotNull(actual);
    for(Map.Entry<String, HiveTypeInfo> pair:  expected.entrySet()) {
      HiveTypeInfo actualType = actual.get(pair.getKey());
      Assert.assertEquals(pair.getValue().getHiveType(), actualType.getHiveType());
    }
  }

  @Test
  public void testConvertRecordToHMSTypeConvert() {
    /* Test for types that require special handling
        [SDC record]   [HiveType after convertRecordToHMSType call]
        char             -> string
        short            -> int
        date             -> string
        decimal          -> decimal
    */
    // Sample data
    Date today = new Date();
    BigDecimal decimalVal =  new BigDecimal(1234567889);

    Record record = RecordCreator.create();
    Map<String, Field> map = new HashMap<>();
    map.put("char", Field.create('a'));
    map.put("short", Field.create((short)10));
    map.put("date", Field.createDate(today));
    map.put("decimal", Field.create(decimalVal));
    record.set(Field.create(map));

    Map<String, HiveTypeInfo> expected = new LinkedHashMap<>();
    expected.put("char", generatePrimitiveTypeInfo(HiveType.STRING));
    expected.put("short", generatePrimitiveTypeInfo(HiveType.INT));
    expected.put("date", generatePrimitiveTypeInfo(HiveType.STRING));
    expected.put("decimal", generatePrimitiveTypeInfo(HiveType.DECIMAL));

    Map<String, HiveTypeInfo> actual = null;
    try {
      actual = HiveMetastoreUtil.convertRecordToHMSType(record);
    } catch (StageException e){
      Assert.fail("convertRecordToHMSType threw StageException:" + e.getMessage());
    }
    Assert.assertNotNull(actual);
    // Test if all the HiveTypeInfo has the same HiveType as expected
    for(Map.Entry<String, HiveTypeInfo> pair:  expected.entrySet()) {
      HiveTypeInfo actualType = actual.get(pair.getKey());
      Assert.assertEquals(pair.getValue().getHiveType(), actualType.getHiveType());
    }

    // Test if the Field type and values in original Record are converted correctly
    Map<String, Field> list = record.get().getValueAsListMap();
    Field f1 = list.get("char");
    Assert.assertEquals(f1.getType(), Field.Type.STRING);
    Assert.assertTrue(f1.getValue() instanceof String);
    Assert.assertEquals(f1.getValueAsChar(), 'a');

    Field f2 = list.get("short");
    Assert.assertEquals(f2.getType(), Field.Type.INTEGER);
    Assert.assertTrue(f2.getValue() instanceof Integer);
    Assert.assertEquals(f2.getValueAsInteger(), 10);

    Field f3 = list.get("date");
    Assert.assertEquals(f3.getType(), Field.Type.STRING);
    Assert.assertTrue(f3.getValue() instanceof String);
    Assert.assertEquals(f3.getValueAsString(), today.toString());

    Field f4 = list.get("decimal");
    Assert.assertEquals(f4.getType(), Field.Type.DECIMAL);
    Assert.assertTrue(f4.getValue() instanceof BigDecimal);
    Assert.assertEquals(f4.getValueAsDecimal().scale(), decimalVal.scale());
    Assert.assertEquals(f4.getValueAsDecimal().precision(), decimalVal.precision());
    Assert.assertEquals(f4.getValueAsDecimal().toString(), decimalVal.toString());
  }
}