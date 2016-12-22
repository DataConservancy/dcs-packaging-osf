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

/**
 * @author Elliot Metsger (emetsger@jhu.com)
 */
public class Links {
    private String first;

    private String last;

    private String prev;

    private String next;

    /**
     *
     * @return
     */
    public String getFirst() {
        return first;
    }

    /**
     *
     * @param first
     */
    public void setFirst(final String first) {
        this.first = first;
    }

    /**
     *
     * @return
     */
    public String getLast() {
        return last;
    }

    /**
     *
     * @param last
     */
    public void setLast(final String last) {
        this.last = last;
    }

    /**
     *
     * @return
     */
    public String getPrev() {
        return prev;
    }

    /**
     *
     * @param prev
     */
    public void setPrev(final String prev) {
        this.prev = prev;
    }

    /**
     *
     * @return
     */
    public String getNext() {
        return next;
    }

    /**
     *
     * @param next
     */
    public void setNext(final String next) {
        this.next = next;
    }
}
