package MDRevealer;

import static MDRevealer.Util._;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Logger;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.SpringLayout;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.border.Border;

import java.awt.event.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;

import javax.swing.JOptionPane;
import javax.xml.stream.XMLStreamException;

/*
 * Copyright (C) 2012-2016 Robin Clifford and Robert Clifford
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
public class MDRevealer {

	private static JButton ok, cancel, about;
	private static JCheckBox detailed, summary, spreadsheet, datfile, locdetailed, makecalls;
	private static String version = "2.24";
	private static JFrame mainwindow = new JFrame();
	private static String sourcehospital = "null";
	private static String parser = "null";
	private static String[] defaults = new String[5];
	private static String bp = "null";
	private static String mdr = "null";
	private static String custom_parser = "null";
	private static ArrayList<String> datconfig = new ArrayList<>(0);
	private static JRadioButton Phoenix1 = new JRadioButton("Phoenix1", false);
	private static JRadioButton Vitek1 = new JRadioButton("Vitek1", false);
	private static JRadioButton custom = new JRadioButton("Custom parser", true);
	private static ButtonGroup button_group = new ButtonGroup();

	public static void main(String[] args) throws FileNotFoundException, IOException {
		try {
			File config = new File("config.txt");
			if (config.canRead() == true) {
				BufferedReader configin = null;
				try {
					configin = new BufferedReader(new FileReader(config));
				} catch (FileNotFoundException ex) {
					JOptionPane.showMessageDialog(mainwindow,
							_(MessageFormat.format("Error: {0} not found!", new Object[] { "config.txt" })));
					mainwindow.dispatchEvent(new WindowEvent(mainwindow, WindowEvent.WINDOW_CLOSING));
				}
				for (int i = 0; i < defaults.length; i++) {
					defaults[i] = "null";
				}

				String confstring = configin.readLine();

				while (confstring != null) {
					String[] confsplitter = confstring.split("\t");
					switch (confsplitter[0]) {
					case "Facility":
						sourcehospital = confsplitter[1];
						/* 0-pad the id, just in case */
						while (sourcehospital.length() < 4) {
							sourcehospital = "0" + sourcehospital;
						}
						break;
					case "Default detailed report location":
						defaults[0] = confsplitter[1];
						break;
					case "Default summary location":
						defaults[1] = confsplitter[1];
						break;
					case "Default spreadsheet location":
						defaults[2] = confsplitter[1];
						break;
					case "Default .dat location":
						defaults[3] = confsplitter[1];
						break;
					case "Default localized output":
						defaults[4] = confsplitter[1];
						break;
					case "Default breakpoints file":
						bp = confsplitter[1];
						break;
					case "Default MDR file":
						mdr = confsplitter[1];
						break;
					case ".dat output":
						confstring = configin.readLine();
						confsplitter = confstring.split("\t");
						datconfig.addAll(Arrays.asList(confsplitter));
						break;
					case "Custom parser file":
						custom_parser = confsplitter[1];
						System.out.println(custom_parser);
						break;
					}
					confstring = configin.readLine();
				}
				mainwindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

				detailed = new JCheckBox(_("Detailed report (in English)"));
				summary = new JCheckBox(_("Summary (in English)"));
				spreadsheet = new JCheckBox(_("Spreadsheet (in English)"));
				datfile = new JCheckBox(_(".dat file (in English)"));
				locdetailed = new JCheckBox(_("Detailed report (in your current language)"));
				makecalls = new JCheckBox(_("Ignore calls from the machines and have the program" + " calculate them"));
				JLabel parsing = new JLabel();
				MDRevealer.ButtonListener list = new MDRevealer.ButtonListener();
				JFrame picker = new JFrame("MDRevealer " + version);
				Toolkit tk = Toolkit.getDefaultToolkit();
				Dimension d = tk.getScreenSize();
				picker.setLocationByPlatform(true);
				Double w = d.getWidth();
				picker.setSize((int) (w * 0.60), (int) (d.height * 0.33));
				SpringLayout sl = new SpringLayout();
				picker.setLayout(sl);
				if (parser.equals("spreadsheet")) {
					parsing = new JLabel(_("Parsing a spreadsheet" + '\n'));
				} else if (config.canRead() == true) {
					parsing = new JLabel(MessageFormat.format(_("Parsing a file from facility #{0}" + '\n'),
							new Object[] { sourcehospital }));
				}
				picker.add(parsing);
				JLabel which_reports = new JLabel(_("What reports would you like?") + '\n');
				picker.add(which_reports);
				sl.putConstraint(SpringLayout.WEST, parsing, 20, SpringLayout.WEST, picker);
				sl.putConstraint(SpringLayout.NORTH, parsing, 20, SpringLayout.NORTH, picker);
				sl.putConstraint(SpringLayout.WEST, which_reports, 20, SpringLayout.WEST, picker);
				sl.putConstraint(SpringLayout.SOUTH, which_reports, 20, SpringLayout.SOUTH, parsing);
				Box options = Box.createVerticalBox();
				Border b = BorderFactory.createTitledBorder("");
				options.setBorder(b);
				options.add(detailed);
				options.add(summary);
				if (!parser.equals("spreadsheet")) {
					options.add(spreadsheet);
				}
				if (config.canRead() == true && !parser.equals("dat")) {
					options.add(datfile);
				}
				options.add(locdetailed);
				Box configs = Box.createHorizontalBox();
				configs.setBorder(b);
				if (!parser.equals("spreadsheet")) {
					configs.add(makecalls);
				}
				picker.add(options);
				sl.putConstraint(SpringLayout.WEST, options, 200, SpringLayout.WEST, which_reports);
				sl.putConstraint(SpringLayout.NORTH, options, 10, SpringLayout.NORTH, picker);
				picker.add(configs);
				sl.putConstraint(SpringLayout.WEST, configs, 200, SpringLayout.WEST, which_reports);
				sl.putConstraint(SpringLayout.SOUTH, configs, 30, SpringLayout.SOUTH, options);
				JLabel which_parser = new JLabel(_("Which parser would you like?"));
				picker.add(which_parser);
				sl.putConstraint(SpringLayout.WEST, which_parser, 20, SpringLayout.WEST, picker);
				sl.putConstraint(SpringLayout.SOUTH, which_parser, 20, SpringLayout.SOUTH, configs);
				Box parser_box = Box.createHorizontalBox();
				button_group.add(Phoenix1);
				button_group.add(Vitek1);
				button_group.add(custom);
				parser_box.add(Phoenix1);
				parser_box.add(Vitek1);
				parser_box.add(custom);
				picker.add(parser_box);
				sl.putConstraint(SpringLayout.WEST, which_parser, 20, SpringLayout.WEST, picker);
				sl.putConstraint(SpringLayout.SOUTH, which_parser, 20, SpringLayout.SOUTH, configs);
				sl.putConstraint(SpringLayout.WEST, parser_box, 200, SpringLayout.WEST, which_parser);
				sl.putConstraint(SpringLayout.SOUTH, parser_box, 25, SpringLayout.SOUTH, configs);
				Box buttonbox = Box.createHorizontalBox();
				ok = new JButton(_("OK"));
				ok.addActionListener(list);
				cancel = new JButton(_("Cancel"));
				cancel.addActionListener(list);
				about = new JButton(_("About"));
				about.addActionListener(list);
				buttonbox.add(ok);
				buttonbox.add(cancel);
				buttonbox.add(about);
				picker.add(buttonbox);
				sl.putConstraint(SpringLayout.SOUTH, buttonbox, 30, SpringLayout.SOUTH, which_parser);
				sl.putConstraint(SpringLayout.WEST, buttonbox, 30, SpringLayout.WEST, picker);
				picker.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				picker.setVisible(true);
			} else {
				JOptionPane.showMessageDialog(mainwindow,
						_(MessageFormat.format("Error: {0} not found!", new Object[] { "config.txt" })));
				mainwindow.dispatchEvent(new WindowEvent(mainwindow, WindowEvent.WINDOW_CLOSING));
			}
		} catch (NullPointerException ex) {
			JOptionPane.showMessageDialog(mainwindow, _("Fatal error: ") + ex.getClass() + "\n" + "\n"
					+ ex.getStackTrace()[0] + '\n' + ex.getStackTrace()[1] + '\n' + ex.getStackTrace()[2]);
			mainwindow.dispatchEvent(new WindowEvent(mainwindow, WindowEvent.WINDOW_CLOSING));
		}
	}

	private static class ButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			JFrame temp = new JFrame();
			Boolean deten = detailed.isSelected();
			Boolean sumen = summary.isSelected();
			Boolean spren = spreadsheet.isSelected();
			Boolean daten = datfile.isSelected();
			Boolean locdeten = locdetailed.isSelected();
			Boolean calcen = makecalls.isSelected();
			String parser = null;
			File warnings = new File("warnings.txt");
			if (warnings.canRead() == false) {
				try {
					warnings.createNewFile();
				} catch (IOException ex) {
					Logger.getLogger(MDRevealer.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
			if (Vitek1.isSelected()) {
				parser = "Vitek1";
			} else if (Phoenix1.isSelected()) {
				parser = "Phoenix1";
			} else if (custom.isSelected()) {
				parser = "custom";
			}
			try (FileWriter warnwriter = new FileWriter("warnings.txt")) {
				if (e.getSource() == ok) {
					if (detailed.isSelected() == false && summary.isSelected() == false
							&& spreadsheet.isSelected() == false && datfile.isSelected() == false
							&& locdetailed.isSelected() == false) {
						JOptionPane.showMessageDialog(temp, _("No checkboxes selected!"), _("Error"),
								JOptionPane.INFORMATION_MESSAGE);
					} else {
						switch (parser) {
						case "custom":
							/*
							 * If the custom parser file doesn't exist, throw an
							 * error and exit; otherwise, fall through to the
							 * other parsers.
							 */

							File f = new File(custom_parser);
							if (!f.exists()) {
								JOptionPane.showMessageDialog(mainwindow, _(
										MessageFormat.format("Error: {0} not found!", new Object[] { custom_parser })));
								mainwindow.dispatchEvent(new WindowEvent(mainwindow, WindowEvent.WINDOW_CLOSING));
							}
						case "Vitek1":
							try {
								Parser.parse(mainwindow, deten, sumen, spren, daten, locdeten, sourcehospital, calcen,
										parser, defaults, warnwriter, bp, mdr, datconfig, custom_parser);
							} catch (FileNotFoundException ex) {
								JOptionPane.showMessageDialog(mainwindow,
										_("Error: file not found!") + ex.getStackTrace()[0] + '\n'
												+ ex.getStackTrace()[1] + '\n' + ex.getStackTrace()[2]);
								mainwindow.dispatchEvent(new WindowEvent(mainwindow, WindowEvent.WINDOW_CLOSING));
							} catch (IOException ex) {
								JOptionPane.showMessageDialog(mainwindow,
										_("Fatal error: ") + ex.getClass() + "\n" + "\n" + ex.getStackTrace()[0] + '\n'
												+ ex.getStackTrace()[1] + '\n' + ex.getStackTrace()[2]);
								mainwindow.dispatchEvent(new WindowEvent(mainwindow, WindowEvent.WINDOW_CLOSING));
							} catch (NullPointerException | IndexOutOfBoundsException | ArithmeticException
									| ParseException ex) {
								warnwriter
										.write(_("Fatal error: ") + ex.getClass() + "\n" + "\n" + ex.getStackTrace()[0]
												+ '\n' + ex.getStackTrace()[1] + '\n' + ex.getStackTrace()[2] + '\n'
												+ ex.getStackTrace()[3] + '\n' + ex.getStackTrace()[4]);
								warnwriter.close();
								System.exit(1);
							} catch (XMLStreamException ex) {
								warnwriter.write(_("Unparseable XML file, skipping..."));
							}
							break;
						case "Phoenix1":
							try {
								Parser.parse(mainwindow, deten, sumen, spren, daten, locdeten, sourcehospital, calcen,
										parser, defaults, warnwriter, bp, mdr, datconfig, parser);
							} catch (FileNotFoundException ex) {
								JOptionPane.showMessageDialog(mainwindow,
										_("Error: file not found!") + ex.getStackTrace()[0] + '\n'
												+ ex.getStackTrace()[1] + '\n' + ex.getStackTrace()[2]);
								mainwindow.dispatchEvent(new WindowEvent(mainwindow, WindowEvent.WINDOW_CLOSING));
							} catch (IOException ex) {
								warnwriter
										.write(_("Fatal error: ") + ex.getClass() + "\n" + "\n" + ex.getStackTrace()[0]
												+ '\n' + ex.getStackTrace()[1] + '\n' + ex.getStackTrace()[2] + '\n'
												+ ex.getStackTrace()[3] + '\n' + ex.getStackTrace()[4]);
								mainwindow.dispatchEvent(new WindowEvent(mainwindow, WindowEvent.WINDOW_CLOSING));
							} catch (NullPointerException | IndexOutOfBoundsException | ArithmeticException
									| ParseException ex) {
								warnwriter
										.write(_("Fatal error: ") + ex.getClass() + "\n" + "\n" + ex.getStackTrace()[0]
												+ '\n' + ex.getStackTrace()[1] + '\n' + ex.getStackTrace()[2] + '\n'
												+ ex.getStackTrace()[3] + '\n' + ex.getStackTrace()[4]);
								warnwriter.close();
								System.exit(1);
							} catch (XMLStreamException ex) {
								warnwriter.write(_("Unparseable XML file, skipping..."));
							}
							break;
						default:
							JOptionPane.showMessageDialog(temp,
									_("No config information found!") + '\n' + _("Parsing using default settings..."),
									_("Warning"), JOptionPane.INFORMATION_MESSAGE);
							try {
								Parser.parse(mainwindow, deten, sumen, spren, daten, locdeten, sourcehospital, calcen,
										parser, defaults, warnwriter, bp, mdr, datconfig, parser);
							} catch (FileNotFoundException ex) {
								JOptionPane.showMessageDialog(mainwindow,
										_("Error: file not found!") + ex.getStackTrace()[0] + '\n'
												+ ex.getStackTrace()[1] + '\n' + ex.getStackTrace()[2]);
								mainwindow.dispatchEvent(new WindowEvent(mainwindow, WindowEvent.WINDOW_CLOSING));
							} catch (IOException ex) {
								warnwriter
										.write(_("Fatal error: ") + ex.getClass() + "\n" + "\n" + ex.getStackTrace()[0]
												+ '\n' + ex.getStackTrace()[1] + '\n' + ex.getStackTrace()[2] + '\n'
												+ ex.getStackTrace()[3] + '\n' + ex.getStackTrace()[4]);
								mainwindow.dispatchEvent(new WindowEvent(mainwindow, WindowEvent.WINDOW_CLOSING));
							} catch (NullPointerException | IndexOutOfBoundsException | ArithmeticException
									| ParseException ex) {
								warnwriter.write(_("Fatal error: ") + ex.getClass() + "\n" + "\n" + ex.getStackTrace());
								warnwriter.close();
								System.exit(1);
							} catch (XMLStreamException ex) {
								warnwriter.write(_("Unparseable XML file, skipping..."));
							}
							break;
						}
					}
					/* Close the window (and the program) */
					mainwindow.dispatchEvent(new WindowEvent(mainwindow, WindowEvent.WINDOW_CLOSING));
				} else if (e.getSource() == cancel) {
					mainwindow.dispatchEvent(new WindowEvent(mainwindow, WindowEvent.WINDOW_CLOSING));
				} else if (e.getSource() == about) {
					JOptionPane.showMessageDialog(temp, "MDRevealer " + version + '\n' + MessageFormat.format(
							/*
							 * TRANSLATORS: To quote the GNU Coding Standards,
							 * "Write the word 'Copyright' exactly like that, in
							 * English. Do not translate it into another
							 * language. International treaties recognize the
							 * English word 'Copyright'; translations into other
							 * languages do not have legal significance."
							 */_("Copyright (C) {0}, written by {1} and {2}"),
							new Object[] {
									("2012-2016"), /*
													 * TRANSLATORS : This is a
													 * proper name . See the
													 * gettext manual , section
													 * Names . https : / / www .
													 * gnu . org / software /
													 * gettext / manual /
													 * html_node / Names . html
													 * Pronunciation is like
													 * "rah-bihn cliff-ferd" .
													 */
									_("Robin Clifford"), /*
															 * TRANSLATORS :
															 * This is a proper
															 * name . See the
															 * gettext manual ,
															 * section Names .
															 * https : / / www .
															 * gnu . org /
															 * software /
															 * gettext / manual
															 * / html_node /
															 * Names . html
															 * Pronunciation is
															 * like
															 * "rah-bert cliff-ferd"
															 * .
															 */
									_("Robert Clifford") })
							+ '\n' + _("License GPLv3+: GNU GPL version 3 or later <http://gnu.org/licenses/gpl.html>")
							+ '\n'
							+ /*
								 * TRANSLATORS: Please see https://www
								 * .gnu.org/philosophy/fs- translations.html for
								 * a list of unambigious translations for the
								 * term "free software".
								 */_("This is free software: you are free to change and redistribute it.") + '\n'
							+ _("There is NO WARRANTY, to the extent permitted by law.") + '\n' + '\n'
							+ _("This program uses libintl from GNU gettext 0.18.3, which is copyright (C) 2001,"
									+ "2007 Free Software Foundation, Inc, and licensed under the LGPL version 2.1 or later.")
							+ '\n' + _("For more information, please see LGPL.txt.") + '\n'
							+ _("It also uses Apache Commons Collection, which is copyright (C) 2001-2013 The Apache Software Foundation, and licensed under the Apache License 2.0.")
							+ '\n' + _("For more information, please see Apache2.0.txt."), _("License"),
							JOptionPane.INFORMATION_MESSAGE);
				}
			} catch (IOException ex) {
				Logger.getLogger(MDRevealer.class.getName()).log(Level.SEVERE, null, ex);
				System.exit(1);
			}
		}
	}
}
