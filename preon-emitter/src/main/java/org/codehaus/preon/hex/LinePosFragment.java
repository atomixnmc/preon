/**
 * Copyright (C) 2009-2010 Wilfred Springer
 *
 * This file is part of Preon.
 *
 * Preon is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2, or (at your option) any later version.
 *
 * Preon is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Preon; see the file COPYING. If not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Linking this library statically or dynamically with other modules is making a
 * combined work based on this library. Thus, the terms and conditions of the
 * GNU General Public License cover the whole combination.
 *
 * As a special exception, the copyright holders of this library give you
 * permission to link this library with independent modules to produce an
 * executable, regardless of the license terms of these independent modules, and
 * to copy and distribute the resulting executable under terms of your choice,
 * provided that you also meet, for each linked independent module, the terms
 * and conditions of the license of that module. An independent module is a
 * module which is not derived from or based on this library. If you modify this
 * library, you may extend this exception to your version of the library, but
 * you are not obligated to do so. If you do not wish to do so, delete this
 * exception statement from your version.
 */
package org.codehaus.preon.hex;

import java.io.IOException;

/**
 * A {@link org.codehaus.preon.hex.DumpFragment} that will generate the line position, with the given number of
 * characters, with trailing zeroes if required.
 */
public class LinePosFragment implements DumpFragment {

    private final int size;

    /**
     * Constructs a new instance, accepting the number of character positions reserved for the line number.
     *
     * @param size
     */
    public LinePosFragment(int size) {
        this.size = size;
    }

    public int getSize(int numberOfBytes) {
        return size;
    }

    public void dump(long lineNumber, byte[] buffer, int length, Appendable out) throws IOException {
        String position = Long.toString(lineNumber);
        if (position.length() > size) {
            position = position.substring(position.length() - size);
        } else {
            for (int i = position.length(); i < size; i++) {
                out.append('0');
            }
        }
        out.append(position);
    }
}