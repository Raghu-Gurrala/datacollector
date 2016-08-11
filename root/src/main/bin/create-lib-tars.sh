#!/bin/bash
#
#
# Licensed under the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#   http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.
#
#

VERSION=$1
#STAGE_LIBS=$2
TARGET=$2

STAGE_LIBS=${TARGET}/streamsets-libs

cd ${STAGE_LIBS} || exit
for STAGE_LIB in ${STAGE_LIBS}/*
do
  if [ -d "$STAGE_LIB" ]
  then
    STAGE_NAME=`basename ${STAGE_LIB}`
    echo "Processing stage library: ${STAGE_NAME}"
    echo "tar czf ${TARGET}/${STAGE_NAMER}-${VERSION}.tgz ${STAGE_NAME}/*"
    tar czf ${TARGET}/${STAGE_NAME}-${VERSION}.tgz ${STAGE_NAME}/*
    CURRENT_DIR=`pwd`
    echo "current directory: ${CURRENT_DIR}"
  fi
done