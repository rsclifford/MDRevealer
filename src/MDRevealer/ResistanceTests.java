package MDRevealer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.collections4.CollectionUtils;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/* ResistanceTests - Imports data from files in data/xml and
 * uses it to run tests
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

public class ResistanceTests {

    /*
     * Parse the XML file and returns an ArrayList that tells do_tests() in
     * Resistances what to do.
     */
    private static ArrayList<HashMap<String, String>> parse_test(File rt_input)
            throws XMLStreamException, FileNotFoundException {
        XMLInputFactory xif = XMLInputFactory.newInstance();
        XMLStreamReader xsr = xif.createXMLStreamReader(new FileInputStream(
                rt_input));
        ArrayList<HashMap<String, String>> test = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> hm = new HashMap<String, String>();
        String key = "null";
        while (xsr.hasNext() == true) {

            int constant = xsr.next();

            switch (constant) {
            case XMLStreamConstants.START_ELEMENT:
                key = xsr.getLocalName();
                if (xsr.getAttributeCount() > 0) {
                    for (int i = 0; i < xsr.getAttributeCount(); i++) {
                        hm.put(xsr.getAttributeLocalName(i),
                                xsr.getAttributeValue(i));
                    }
                    test.add(hm);
                    hm = new HashMap<String, String>();
                }
                break;
            case XMLStreamConstants.CHARACTERS:
                if (!xsr.isWhiteSpace()) {
                    hm.put(key, xsr.getText());
                    test.add(hm);
                }
                hm = new HashMap<String, String>();
                break;
            case XMLStreamConstants.END_ELEMENT:
                if (xsr.getLocalName().equals("condition")) {
                    hm.put("condition_over", "true");
                    test.add(hm);
                    hm = new HashMap<String, String>();
                }
                break;
            }
        }
        return test;
    }

    /*
     * Calls parse_test to parse resistance_tests.xml, then does them in order
     * to find special resistances worth noting.
     */
    public static void do_tests(ArrayList<Bacterium> bacteriacollection,
            File rt_input, int i) throws FileNotFoundException,
            XMLStreamException {
        // Put together the actual test
        ArrayList<HashMap<String, String>> tests = parse_test(rt_input);
        ArrayList<String> bacteria = new ArrayList<String>();
        HashMap<String, String> condition = new HashMap<String, String>();
        String drug = "null";
        String value = "null";
        String read_type = "null";
        String comparison = "null";
        String condition_status = "null";
        ArrayList<Integer> conditions_passed = new ArrayList<Integer>();
        ArrayList<Integer> required_conditions = new ArrayList<Integer>();
        int sub_conditions_passed = 0;
        int number_required = 1;
        String cond = "null";
        String ri_value = "null";
        String ri_drug = "null";
        String ri_type = "null";
        for (int step = 0; step < tests.size(); step++) {
            if (tests.get(step).containsKey("condition_over")) {
                for (String bacterium : bacteria) {
                    if (bacteriacollection.get(i).getName().toUpperCase()
                            .contains(bacterium.toUpperCase())
                            || (bacterium.equals("Generic") && bacteriacollection
                                    .get(i).getGeneric() == true)
                            || bacterium.equals("Any")) {
                        if ((read_type.equals("Call") && !cond
                                .equals("failed"))
                                || (read_type.equals("MIC")
                                        && sub_conditions_passed >= number_required && number_required != 0)) {
                            // NullPointerErrors will be the death of me...
                            if (conditions_passed.toString().equals(null)) {
                                conditions_passed.add(Integer
                                        .parseInt(condition.get("number")));
                            } else if (!conditions_passed.toString().contains(
                                    condition.get("number"))) {
                                conditions_passed.add(Integer
                                        .parseInt(condition.get("number")));
                            }
                        }
                    }
                }
                sub_conditions_passed = 0;
                number_required = 1;
            }
            if (tests.get(step).containsKey("test_name")) {
                // Reset everything
                bacteria = new ArrayList<String>();
                drug = "null";
                value = "null";
                read_type = "null";
                comparison = "null";
                condition_status = "null";
                conditions_passed = new ArrayList<Integer>();
                required_conditions = new ArrayList<Integer>();
                sub_conditions_passed = 0;
                number_required = 0;
                cond = "null";
            } else if (tests.get(step).containsKey("bacterium")) {
                bacteria.add(tests.get(step).get("bacterium"));
            } else if (tests.get(step).containsKey("default")
                    && tests.get(step).containsKey("number")) {
                condition = tests.get(step);
                cond = tests.get(step).get("default");
                if (tests.get(step).containsKey("number_required")) {
                    number_required = Integer.parseInt(tests.get(step).get(
                            "number_required"));
                }
            } else if (tests.get(step).containsKey("name")) {
                drug = tests.get(step).get("name");
            } else if (tests.get(step).containsKey("value")
                    && tests.get(step).containsKey("read_type")) {
                value = tests.get(step).get("value");
                read_type = tests.get(step).get("read_type");
                if (read_type.equals("MIC")) {
                    comparison = tests.get(step).get("comparison");
                }
            } else if (tests.get(step).containsKey("condition_status")) {
                condition_status = tests.get(step).get("condition_status");

                // Finally do the actual tests
                for (int j = 0; j < bacteriacollection.get(i).drugSize(); j++) {
                    for (String bacterium : bacteria) {
                        if (bacteriacollection.get(i).getName().toUpperCase()
                                .contains(bacterium.toUpperCase())
                                || (bacterium.equals("Generic") && bacteriacollection
                                        .get(i).getGeneric() == true)
                                || bacterium.equals("Any")) {
                            if (bacteriacollection.get(i).getDrug(j)
                                    .equals(drug)) {
                                if (read_type.equals("Call")) {
                                    if ((bacteriacollection.get(i).getCall(j)
                                            .equals(value))) {
                                        if (condition_status.equals("failed")) {
                                            cond = "failed";
                                        } else if (condition_status
                                                .equals("+")) {
                                            sub_conditions_passed++;
                                        } else if (condition_status
                                                .equals("-")) {
                                            sub_conditions_passed--;
                                        } else if (condition_status
                                                .equals("passed")) {
                                            cond = "passed";
                                        }
                                    }
                                } else if (read_type.equals("MIC")) {
                                    switch (comparison) {
                                    case "greater_than":
                                        if (bacteriacollection.get(i)
                                                .getMIC(j) > Double
                                                .parseDouble(value)) {
                                            if (condition_status
                                                    .equals("failed")) {
                                                cond = "failed";
                                            } else if (condition_status
                                                    .equals("+")) {
                                                sub_conditions_passed++;
                                            } else if (condition_status
                                                    .equals("-")) {
                                                sub_conditions_passed--;
                                            } else if (condition_status
                                                    .equals("passed")) {
                                                cond = "passed";
                                            }
                                        }
                                        break;
                                    case "less_than":
                                        if (bacteriacollection.get(i)
                                                .getMIC(j) < Double
                                                .parseDouble(value)) {
                                            if (condition_status
                                                    .equals("failed")) {
                                                cond = "failed";
                                            } else if (condition_status
                                                    .equals("+")) {
                                                sub_conditions_passed++;
                                            } else if (condition_status
                                                    .equals("-")) {
                                                sub_conditions_passed--;
                                            } else if (condition_status
                                                    .equals("passed")) {
                                                cond = "passed";
                                            }
                                        }
                                        break;
                                    case "greater_than_equal":
                                        if (bacteriacollection.get(i)
                                                .getMIC(j) >= Double
                                                .parseDouble(value)) {
                                            if (condition_status
                                                    .equals("failed")) {
                                                cond = "failed";
                                            } else if (condition_status
                                                    .equals("+")) {
                                                sub_conditions_passed++;
                                            } else if (condition_status
                                                    .equals("-")) {
                                                sub_conditions_passed--;
                                            } else if (condition_status
                                                    .equals("passed")) {
                                                cond = "passed";
                                            }
                                        }
                                        break;
                                    case "less_than_equal":
                                        if (bacteriacollection.get(i)
                                                .getMIC(j) <= Double
                                                .parseDouble(value)) {
                                            if (condition_status
                                                    .equals("failed")) {
                                                cond = "failed";
                                            } else if (condition_status
                                                    .equals("+")) {
                                                sub_conditions_passed++;
                                            } else if (condition_status
                                                    .equals("-")) {
                                                sub_conditions_passed--;
                                            } else if (condition_status
                                                    .equals("passed")) {
                                                cond = "passed";
                                            }
                                        }
                                        break;
                                    default:
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            } else if (tests.get(step).containsKey("required_condition")) {
                for (String bacterium : bacteria) {
                    if (bacteriacollection.get(i).getName().toUpperCase()
                            .contains(bacterium.toUpperCase())
                            || (bacterium.equals("Generic") && bacteriacollection
                                    .get(i).getGeneric() == true)
                            || bacterium.equals("Any")) {
                        if (!required_conditions.toString().contains(
                                condition.get("number"))) {
                            required_conditions.add(Integer.parseInt(tests
                                    .get(step).get("required_condition")));
                        }
                    }
                }
            } else if (tests.get(step).containsKey("required_if")) {
                ri_value = tests.get(step - 1).get("value");
                ri_type = tests.get(step - 1).get("read_type");
                ri_drug = tests.get(step - 1).get("drug");
                for (int j = 0; j < bacteriacollection.get(i).drugSize(); j++) {
                    for (String bacterium : bacteria) {
                        if (bacteriacollection.get(i).getName()
                                .contains(bacterium)
                                || (bacterium.equals("Generic") && bacteriacollection
                                        .get(i).getGeneric() == true)
                                || bacterium.equals("Any")) {
                            if (bacteriacollection.get(i).getDrug(j)
                                    .equals(ri_drug)) {
                                if (ri_type.equals("Call")) {
                                    if ((bacteriacollection.get(i).getCall(j)
                                            .equals(ri_value))) {
                                        required_conditions.add(Integer
                                                .parseInt(tests.get(step).get(
                                                        "required_if")));
                                    }
                                } else if (ri_type.equals("MIC")) {
                                    switch (comparison) {
                                    case "greater_than":
                                        if (bacteriacollection.get(i)
                                                .getMIC(j) > Double
                                                .parseDouble(value)) {
                                            required_conditions
                                                    .add(Integer
                                                            .parseInt(tests
                                                                    .get(step)
                                                                    .get("required_if")));
                                        }
                                        break;
                                    case "less_than":
                                        if (bacteriacollection.get(i)
                                                .getMIC(j) < Double
                                                .parseDouble(value)) {
                                            required_conditions
                                                    .add(Integer
                                                            .parseInt(tests
                                                                    .get(step)
                                                                    .get("required_if")));
                                        }
                                        break;
                                    case "greater_than_equal":
                                        if (bacteriacollection.get(i)
                                                .getMIC(j) >= Double
                                                .parseDouble(value)) {
                                            required_conditions
                                                    .add(Integer
                                                            .parseInt(tests
                                                                    .get(step)
                                                                    .get("required_if")));
                                        }
                                        break;
                                    case "less_than_equal":
                                        if (bacteriacollection.get(i)
                                                .getMIC(j) <= Double
                                                .parseDouble(value)) {
                                            required_conditions
                                                    .add(Integer
                                                            .parseInt(tests
                                                                    .get(step)
                                                                    .get("required_if")));
                                        }
                                        break;
                                    default:
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            } else if (tests.get(step).containsKey("result")) {
                ArrayList<Integer> common_conditions = (ArrayList<Integer>) CollectionUtils
                        .retainAll(conditions_passed, required_conditions);
                if (common_conditions.toString().equals(
                        required_conditions.toString())
                        && conditions_passed.size() > 0) {
                    if (tests.get(step).get("result").equals("Carb")) {
                        bacteriacollection.get(i).setCarb(true);
                    } else if (tests.get(step).get("result").equals("ESBL")) {
                        bacteriacollection.get(i).setESBL("true");
                    } else if (tests.get(step).get("result").equals("VRE")) {
                        bacteriacollection.get(i).setVRE(true);
                    } else if (tests.get(step).get("result").equals("MRSA")) {
                        bacteriacollection.get(i).setOxa(true);
                    } else {
                        bacteriacollection.get(i).setCustom(
                                tests.get(step).get("result"));
                    }
                }
            }
        }
    }
}
