/**
 * Licensed to the Apache Software Foundation (ASF) under one
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
package com.streamsets.pipeline.restapi;

import com.streamsets.pipeline.stagelibrary.StageLibrary;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Locale;

@Path("/v1/stage-library")
public class StageLibraryResource {
  private final StageLibrary stageLibrary;
  private final Locale locale;

  @Inject
  public StageLibraryResource(StageLibrary stageLibrary, Locale locale) {
    this.stageLibrary = stageLibrary;
    this.locale = locale;
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Response getAllRegisteredModules() {
    return Response.ok().type(MediaType.APPLICATION_JSON).entity(stageLibrary.getStages(locale)).build();
  }

}
