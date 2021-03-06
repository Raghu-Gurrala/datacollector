/*
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

package com.streamsets.pipeline.lib.xml.xpath;

public abstract class Constants {
  public static final String PATH_SEPARATOR="/";
  public static final String WILDCARD="*";
  public static final String ROOT_ELEMENT_PATH = Constants.WILDCARD+"[1]";
  public static final char NAMESPACE_PREFIX_SEPARATOR = ':';

  public static final String XML_RECORD_ELEMENT_DESCRIPTION = "XML element that acts as a record delimiter." +
      "  This may be an element name, with elements having that local name directly under the root as records." +
      "  Or, it can be a simplified XPath expression (see docs), with elements matching the XPath expression" +
      " as records.  Leaving it blank will treat the whole XML document as one record.";

  public static final String XPATH_NAMESPACE_CONTEXT_DESCRIPTION = "Namespace context to use if the delimiter" +
      " is an XPath expression.  This should map namespace prefixes to URIs.  Any namespace prefix that is used" +
      " in the record separator expression must be defined here.";
}
