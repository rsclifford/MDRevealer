package MDRevealer;

import java.text.MessageFormat;
import static MDRevealer.Util._;
import java.io.FileWriter;
import java.io.IOException;

/* call - Contains functions used with calls
 * Copyright (C) 2012-2013 Robin Clifford and Robert Clifford
 * This program is part of MDRevealer.
 * MDRevealer is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MDRevealer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
public class call {

    /* Checks alternate resistance/sensitivity indicators. */
    public static String convert(String call, FileWriter warnwriter)
            throws IOException {
        if (call.equalsIgnoreCase("POS") || call.equalsIgnoreCase("+")
                || call.equalsIgnoreCase("BLAC")
                || call.equalsIgnoreCase("ESBL")
                || call.equalsIgnoreCase("R*") || call.equalsIgnoreCase("YES")
                || call.equalsIgnoreCase("Y") || call.equalsIgnoreCase("R")) {
            return "R";
        } else if (call.equalsIgnoreCase("NEG") || call.equalsIgnoreCase("-")
                || call.equalsIgnoreCase("S")) {
            return "S";
        } else if (call.equalsIgnoreCase("N") || call.equalsIgnoreCase("TFG")
                || call.equalsIgnoreCase("X") || call.equalsIgnoreCase("N/R")) {
            return "N";
        } else if (call.equalsIgnoreCase("I")) {
            return "I";
        } else if (call.equalsIgnoreCase(" ") || call.equals("")) {
            return " ";
        } else {
            warnwriter.write(MessageFormat.format(
                    _("Warning: the call name \"{0}\" is not"
                            + " known to the program. Either this is a "
                            + "typo, or it is an error in the program."),
                    new Object[] { call }) + '\n');
            return call;
        }
    }
}
