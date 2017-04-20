package MDRevealer;

import java.text.MessageFormat;
import java.util.ArrayList;
import static MDRevealer.Util._;
import java.io.FileWriter;
import java.io.IOException;

/* mtf - Parses hospital names
 * 
 * Copyright (C) 2012-2014 Robin Clifford and Robert Clifford
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
public class mtf {

    private String oldname;
    private String newname;

    public mtf() {
        oldname = "temp";
        newname = "temp";
    }

    public mtf(String aleph, String beth) {
        oldname = aleph;
        newname = beth;
    }

    public String getOld() {
        return this.oldname;
    }

    public void setOld(String oldname) {
        this.oldname = oldname;
    }

    public String getNew() {
        return this.newname;
    }

    public void setNew(String newname) {
        this.newname = newname;
    }

    public static String convert(String tempname,
            ArrayList<mtf> mtfcollection, FileWriter warnwriter)
            throws IOException {
        for (int i = 0; i < mtfcollection.size(); i++) {
            if (tempname.equals(mtfcollection.get(i).getOld())) {
                return tempname + " " + mtfcollection.get(i).getNew();
            }
        }
        warnwriter.write(MessageFormat.format(
                _("Warning: the hospital name \"{0}\" does not "
                        + "appear in {1}. Either this is a typo, or "
                        + "the name is missing from the file."), new Object[] {
                        tempname, "hospitals.txt" }) + '\n');
        return tempname;
    }
}
