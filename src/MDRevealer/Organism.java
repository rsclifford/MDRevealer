package MDRevealer;

import static MDRevealer.Util._;
import java.io.FileWriter;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;

/* organism - Converts organism names to shorter ones
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
public class Organism {

    private String oldname;
    private String newname;
    private String shortname;

    public Organism() {
        oldname = "temp";
        newname = "temp";
        shortname = "temp";
    }

    public Organism(String aleph, String beth, String gimmel) {
        oldname = aleph;
        newname = beth;
        shortname = gimmel;
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

    public String getShort() {
        return this.shortname;
    }

    public void setShort(String shortname) {
        this.shortname = shortname;
    }

    public static String convert(String tempname,
            ArrayList<Organism> organismcollection, FileWriter warnwriter)
            throws IOException {
        for (int i = 0; i < organismcollection.size(); i++) {
            if (tempname.toUpperCase().equals(
                    organismcollection.get(i).getOld())) {
                return organismcollection.get(i).getNew();
            }
        }
        warnwriter.write(MessageFormat.format(
                _("Warning: the organism name \"{0}\" does not "
                        + "appear in {1}. Either this is a typo, or "
                        + "the name is missing from the file."), new Object[] {
                        tempname, "organisms.txt" }) + '\n');
        return tempname;
    }

    public static String convertShort(String tempname,
            ArrayList<Organism> organismcollection, FileWriter warnwriter)
            throws IOException {
        for (int i = 0; i < organismcollection.size(); i++) {
            if (tempname.toUpperCase().equals(
                    organismcollection.get(i).getOld())) {
                return organismcollection.get(i).getShort();
            }
        }
        warnwriter.write(MessageFormat.format(
                _("Warning: the organism name \"{0}\" does not"
                        + " appear in {1}. Either this is a typo, or"
                        + " the name is missing from the file."),
                new Object[] { tempname, "organisms.txt" }) + '\n');
        return tempname;
    }
}
