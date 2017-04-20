package MDRevealer;

/*
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
import java.util.ArrayList;

public class DrugName {

    private ArrayList<String> nicknames;
    private String propername;

    public DrugName() {
        nicknames = new ArrayList<>(0);
        propername = "temp";
    }

    public DrugName(String goodname) {
        nicknames = new ArrayList<>(0);
        propername = goodname;
    }

    public String getNickname(int index) {
        if (nicknames.get(index).isEmpty() == false) {
            return nicknames.get(index);
        } else {
            return " ";
        }
    }

    public void addNickname(String s) {
        this.nicknames.add(s);
    }

    public int nicknameSize() {
        return this.nicknames.size();
    }

    public String getProperName() {
        return propername;
    }

    public void setProperName(String s) {
        this.propername = s;
    }
}
