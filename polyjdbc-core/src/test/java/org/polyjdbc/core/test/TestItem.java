/*
 * Copyright 2013 Adam Dubiel, Przemek Hertel.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.polyjdbc.core.test;

/**
 *
 * @author Adam Dubiel
 */
public class TestItem {

    private String pseudo;

    private int count;

    public TestItem(String pseudo, int count) {
        this.pseudo = pseudo;
        this.count = count;
    }

    public String getPseudo() {
        return pseudo;
    }

    public int getCount() {
        return count;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + (this.pseudo != null ? this.pseudo.hashCode() : 0);
        hash = 67 * hash + this.count;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TestItem other = (TestItem) obj;
        return other.hashCode() == hashCode();
    }

    @Override
    public String toString() {
        return pseudo + " " + count;
    }
}
