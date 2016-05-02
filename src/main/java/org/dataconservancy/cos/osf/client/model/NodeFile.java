/*
 * Copyright 2016 Johns Hopkins University
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
package org.dataconservancy.cos.osf.client.model;

import org.joda.time.DateTime;

import java.util.Set;

/**
 * Created by esm on 5/2/16.
 */
public class NodeFile {

    private String kind;

    private String name;

    private String materialized_path;

    private DateTime date_created;

    private DateTime date_modified;

    private String provider;

    private String path;

    private long size;

    private String id;

    private Set<Checksum> hashes;

}
