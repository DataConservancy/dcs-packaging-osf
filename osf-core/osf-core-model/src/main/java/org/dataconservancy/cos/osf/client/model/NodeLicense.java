/*
 * Copyright 2017 Johns Hopkins University
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

import java.util.List;

/**
 * Represents licence information associated with a node.
 *
 * @author Elliot Metsger (emetsger@jhu.edu)
 */
public class NodeLicense {

    private List<String> copyright_holders;

    private String year;

    /**
     *
     * @return
     */
    public List<String> getCopyright_holders() {
        return copyright_holders;
    }

    /**
     *
     * @param copyright_holders
     */
    public void setCopyright_holders(final List<String> copyright_holders) {
        this.copyright_holders = copyright_holders;
    }

    /**
     *
     * @return
     */
    public String getYear() {
        return year;
    }

    /**
     *
     * @param year
     */
    public void setYear(final String year) {
        this.year = year;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final NodeLicense that = (NodeLicense) o;

        if (copyright_holders != null ?
                !copyright_holders.equals(that.copyright_holders) : that.copyright_holders != null) {
            return false;
        }

        return year != null ? year.equals(that.year) : that.year == null;
    }

    @Override
    public int hashCode() {
        int result = copyright_holders != null ? copyright_holders.hashCode() : 0;
        result = 31 * result + (year != null ? year.hashCode() : 0);
        return result;
    }
}
