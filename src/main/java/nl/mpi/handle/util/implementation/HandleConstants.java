/*
 * Copyright (C) 2015 Max Planck Institute for Psycholinguistics
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package nl.mpi.handle.util.implementation;

import java.util.regex.Pattern;

/**
 *
 * @author guisil
 */
public class HandleConstants {
    
    public static final String HDL_SHORT_PROXY = "hdl";
    public static final String HDL_LONG_PROXY = "http://hdl.handle.net/";
    public static final Pattern HANDLE_PATTERN = Pattern.compile("^[^/]+/[^/]+$");
}
