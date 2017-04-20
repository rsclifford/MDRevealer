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

public class MDR {

    private String orgname;
    private ArrayList<String> category;
    private ArrayList<String> drugname;
    private int totalcats;

    public MDR() {
        orgname = "temp";
        category = new ArrayList<>(0);
        drugname = new ArrayList<>(0);
        totalcats = 0;
    }

    public MDR(String a) {
        orgname = a;
        category = new ArrayList<>(0);
        drugname = new ArrayList<>(0);
        totalcats = 0;
    }

    public String getName() {
        return orgname;
    }

    public void setName(String orgname) {
        this.orgname = orgname;
    }

    public String getCategory(int i) {
        return category.get(i);
    }

    public void addCategory(String s) {
        this.category.add(s);
    }

    public String getDrugname(int i) {
        return drugname.get(i);
    }

    public void addDrugname(String s) {
        this.drugname.add(s);
    }

    public int getCategorySize() {
        return this.category.size();
    }

    public int getDrugnameSize() {
        return this.drugname.size();
    }

    public int getTotalcats() {
        return this.totalcats;
    }

    public void setTotalcats(int totalcats) {
        this.totalcats = totalcats;
    }
}
