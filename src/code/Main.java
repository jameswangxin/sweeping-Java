package code;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.Timer;
import javax.swing.UIManager;

// ���ڱ�ʶÿһ��Label �����ֵ�ͼƬ
enum Status {
	NONE(0), ONE(1), TWO(2), THREE(3), FOUR(4), FIVE(5), SIX(6), SEVEN(7), EIGHT(8), 
	BUTTON(9), FLAG(10), BOOM(11), DOUBT(12), FAILED(13), FAILFLAG(14), PROMPT(15);
	private int value;
	private Status(int v) {
		value = v;
	}
	public int getValue() {
		return value;
	}
	public static Status getStatus(int v) {
		return Status.values()[v];
	}
}

// ���ڶ԰˸�����Label ���ظ��ж�
class Direction {
	static final int[][] Dir = {{-1, 0}, {0, 1}, {1, 0}, {0, -1}, 
			{1, -1}, {-1, -1}, {-1, 1}, {1, 1}};
}

// ���ڼ�¼��ͬ�Ѷȶ�Ӧ��ɨ������
class Difficult {
	static final int[][] Diff = {{9, 9, 10}, {16, 16, 40}, {16, 30, 99}};
}

// ���а�����
class Rank {
	class Item {
		String name;
		int score;
		public Item() {
			name = new String("����");
			score = 999;
		}
		public void setItem(String n, int s) {
			name = n;
			score = s;
		}
	}
	
	Item[] item;
	public Rank(String filename) {
		String name;
		int score;
		item = new Item[3];
		for(int i = 0; i < 3; ++i) {
			item[i] = new Item();
		}
		File file = new File(filename);
		FileInputStream in = null;
		try {
			in = new FileInputStream(file);
			try {
				ObjectInputStream cin = new ObjectInputStream (in);
				for(int i = 0; i < 3; ++i) {
					try {
						score = cin.readInt();
						name = (String)cin.readObject();
						item[i].setItem(name, score);
					} catch (ClassNotFoundException e) {
						JOptionPane.showMessageDialog(null, "�ļ��𻵣���ɾ���ļ�" + filename + " ����������", 
								"����", JOptionPane.ERROR_MESSAGE);
						System.exit(0);
					}
				}
				cin.close();
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, "�ļ��𻵣���ɾ���ļ�" + filename + " ����������", 
						"����", JOptionPane.ERROR_MESSAGE);
				System.exit(0);
			}
		} catch (FileNotFoundException e) {
			writeFile(filename);
		}
	}
	public void writeFile(String filename) {
		File file = new File(filename);
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(file);
			try {
				ObjectOutputStream cout = new ObjectOutputStream(out);
				for(int i = 0; i < 3; ++i) {
					cout.writeInt(item[i].score);
					cout.writeObject(item[i].name);
				}
				cout.close();
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, "�ļ��𻵣���ɾ���ļ�" + filename + " ����������", 
						"����", JOptionPane.ERROR_MESSAGE);
				System.exit(0);
			}
		} catch (FileNotFoundException e1) {
			JOptionPane.showMessageDialog(null, "�ļ��𻵣���ɾ���ļ�" + filename + " ����������", 
					"����", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}
	}
	public void reSet() {
		for(int i = 0; i < 3; ++i) {
			item[i] = new Item();
		}
	}
}

class Demine extends JFrame{
	private static final long serialVersionUID = 1L;
	class Panel extends JPanel {
		private static final long serialVersionUID = 1L;
		
		class Label extends JLabel {
			private static final long serialVersionUID = 1L;
			static final int WIDTH = 30;
			static final int HEIGHT = 30;
			int mouse;
			Status status;
			Point loc;
			
			Label(Status s, Point p) {
				super(new ImageIcon("image/" + s.toString() + ".png"));
				mouse = 0;
				status = s;
				loc = p;
				addMouseListener(new MouseAdapter() {
					public void mousePressed(MouseEvent e) {
						mouse += e.getButton();
						if(play == 1 && mouse == 4) {
							// �԰˸������ϱ�ǵ����ӽ��м���
							mouse = 0;
							int cnt = 0;
							for(int i = 0; i < Direction.Dir.length; ++i) {
								int xx = loc.x + Direction.Dir[i][0];
								int yy = loc.y + Direction.Dir[i][1];
								if(xx >= 0 && yy >= 0 && xx < row && yy < col 
										&& label[xx][yy].status == Status.FLAG) {
									++cnt;
								}
							}
							
							if(cnt == mark[loc.x][loc.y] && status != Status.BUTTON 
								&& status != Status.DOUBT && status != Status.FLAG) {
								// ���������ȷ���͵����Χ��BUTTON
								for(int i = 0; i < Direction.Dir.length; ++i) {
									int xx = loc.x + Direction.Dir[i][0];
									int yy = loc.y + Direction.Dir[i][1];
									if(xx >= 0 && yy >= 0 && xx < row && yy < col 
											&& (label[xx][yy].status == Status.DOUBT 
											|| label[xx][yy].status == Status.BUTTON)) {
										clickLabel(new Point(xx, yy));
									}
								}
							} else {
								// ���������ϣ������PROMPT ��ʾ
								for(int i = 0; i < Direction.Dir.length; ++i) {
									int xx = loc.x + Direction.Dir[i][0];
									int yy = loc.y + Direction.Dir[i][1];
									if(xx >= 0 && yy >= 0 && xx < row && yy < col 
											&& label[xx][yy].status == Status.BUTTON) {
										label[xx][yy].setStatus(Status.PROMPT);
									}
								}
							}
						}
					}
					public void mouseReleased(MouseEvent e) {
						mouse -= e.getButton();
						if(mouse < 0) {
							// �������PROMPT ��ʾ����ָ����ܵ�BUTTON
							mouse = 0;
							for(int i = 0; i < Direction.Dir.length; ++i) {
								int xx = loc.x + Direction.Dir[i][0];
								int yy = loc.y + Direction.Dir[i][1];
								if(xx >= 0 && yy >= 0 && xx < row && yy < col 
										&& label[xx][yy].status == Status.PROMPT) {
									label[xx][yy].setStatus(Status.BUTTON);
								}
							}
						} else if(e.getButton() == 1) {
							// ����¼�
							if(play == 1 && (status == Status.BUTTON || status == Status.DOUBT)) {
								clickLabel(loc);
							} else if(play == 0){
								play = 1;
								timecal.setText("1");
								timer.restart();
								Srand(loc);
								clickLabel(loc);
							} else if(play == -1) {
								Init();
							}
						} else if(e.getButton() == 3) {
							// �һ��¼�
							if(play != -1) {
								switch(status) {
								case BUTTON: {
									int temp = Integer.parseInt(minecnt.getText());
									if(temp > 0) {
										setStatus(Status.FLAG);
										--temp;
										minecnt.setText(String.valueOf(temp));
									}
									break;
								}
								case FLAG: {
									setStatus(Status.DOUBT);
									int temp = Integer.parseInt(minecnt.getText());
									++temp;
									minecnt.setText(String.valueOf(temp));
									break;
								}
								case DOUBT: setStatus(Status.BUTTON); break;
								default: break;
								}
							} else {
								Init();
							}
						}
					}
				});
			}
			Label(Status s, int x, int y) {
				this(s, new Point(x, y));
			}
			void setStatus(Status s) {
				status = s;
				setIcon(new ImageIcon("image/" + s.toString() + ".png"));
			}
		}
		
		static final int X = 50;
		static final int Y = 30;
		static final int DISY = 10;
		static final int DISX = 5;
		int row;
		int col;
		int mines;
		int open;
		int play;	// 0  ��ʾ��û��ʼ��1 ��ʾ���ڽ��У�-1 ��ʾ�Ѿ�����
		int[][] mark;
		JLabel timecal;
		JLabel minecnt;
		Label[][] label;
		JPanel game;
		Timer timer;
		Demine frame;
		
		public Panel(Demine f, int r, int c, int m) {
			super(null);
			play = 0;
			row = r;
			col = c;
			mines = m;
			open = 0;
			label = new Label[r][c];
			mark = new int[r][c];
			frame = f;
			
			// ���ɨ����Ϸ������
			game = new JPanel(new GridLayout(r, c, 0, 0));
			game.setBounds(X, Y, col * Label.WIDTH, row * Label.HEIGHT);
			for(int i = 0; i < r; ++i) {
				for(int j = 0; j < c; ++j) {
					mark[i][j] = Status.NONE.getValue();
					label[i][j] = new Label(Status.BUTTON, i, j);
					game.add(label[i][j]);
				}
			}
			add(game);
			addComponent();
		}
		public Panel(Demine f, int r, int c, int m, Label[][] l, int[][] mar) {
			super(null);
			play = 0;
			row = r;
			col = c;
			mines = m;
			open = 0;
			label = l;
			mark = mar;
			frame = f;
			
			// ���ɨ����Ϸ������
			game = new JPanel(new GridLayout(r, c, 0, 0));
			game.setBounds(X, Y, col * Label.WIDTH, row * Label.HEIGHT);
			for(int i = 0; i < r; ++i) {
				for(int j = 0; j < c; ++j) {
					game.add(label[i][j]);
				}
			}
			add(game, 0);
			addComponent();
		}
		// ����ɨ�ײ���
		public void reSize(int r, int c, int m) {
			removeAll();
			row = r;
			col = c;
			mines = m;
			play = 0;
			open = 0;
			mark = new int[row][col];
			label = new Label[row][col];
			timer.stop();
			
			// ���ɨ����Ϸ������
			game = new JPanel(new GridLayout(r, c, 0, 0));
			game.setBounds(X, Y, col * Label.WIDTH, row * Label.HEIGHT);
			for(int i = 0; i < r; ++i) {
				for(int j = 0; j < c; ++j) {
					mark[i][j] = Status.NONE.getValue();
					label[i][j] = new Label(Status.BUTTON, i, j);
					game.add(label[i][j]);
				}
			}
			add(game);
			addComponent();
			repaint();
			validate();
			ReSize();
		}
		// ��ʼ��Panel
		private void Init() {
			timecal.setText("0");
			minecnt.setText(String.valueOf(mines));
			play = 0;
			open = 0;
			for(int i = 0; i < row; ++i) {
				for(int j = 0; j < col; ++j) {
					label[i][j].setStatus(Status.BUTTON);
					mark[i][j] = Status.NONE.getValue();
				}
			}
		}
		// �������ɨ�ײ��֣�p ��ʾ��������Label ���꣬�����ڸõ��������
		private void Srand(Point p) {
			Random rand = new Random();
			int r, c;
			
			for(int i = 0; i < mines; ++i) {
				do {
					r = rand.nextInt(row);
					c = rand.nextInt(col);
				} while((r == p.x && c == p.y) || mark[r][c] == Status.FAILED.getValue());
				mark[r][c] = Status.FAILED.getValue();
			}
			
			for(int i = 0; i < row; ++i) {
				for(int j = 0; j < col; ++j) {
					if(mark[i][j] == Status.FAILED.getValue()) {
						for(int k = 0; k < Direction.Dir.length; ++k) {
							int ii = i + Direction.Dir[k][0];
							int jj = j + Direction.Dir[k][1];
							if(ii >= 0 && jj >= 0 && ii < row && jj < col 
									&& mark[ii][jj] != Status.FAILED.getValue()) {
								++mark[ii][jj];
							}
						}
					}
				}
			}
		}
		// ���ĳһ��Label �ᴥ�����¼���p ��ʾ�����Label ����
		private void clickLabel(Point p) {
			if(label[p.x][p.y].status == Status.BUTTON || label[p.x][p.y].status == Status.DOUBT) {
				if(mark[p.x][p.y] != Status.FAILED.getValue()) {
					// δ���������
					Queue<Point> que = new LinkedList<Point>();
					Boolean[][] visit = new Boolean[row][col];
					for(int i = 0; i < row; ++i) {
						Arrays.fill(visit[i], false);
					}
					
					Point temp = p;
					que.offer(temp);
					label[temp.x][temp.y].setStatus(Status.getStatus(mark[temp.x][temp.y]));
					++open;
					while((temp = que.poll()) != null) {
						if(mark[temp.x][temp.y] == 0) {
							visit[temp.x][temp.y] = true;
							for(int i = 0; i < Direction.Dir.length; ++i) {
								int xx = temp.x + Direction.Dir[i][0];
								int yy = temp.y + Direction.Dir[i][1];
								if(xx >= 0 && yy >= 0 && xx < row && yy < col && !visit[xx][yy]) {
									if(label[xx][yy].status == Status.BUTTON || label[xx][yy].status == Status.DOUBT) {
										++open;
									}
									label[xx][yy].setStatus(Status.getStatus(mark[xx][yy]));
									if(mark[xx][yy] == 0) {
										que.offer(new Point(xx, yy));
									}
								}
							}
						}
					}
					if(open == row * col - mines) {
						play = -1;
						timer.stop();
						if(Integer.parseInt(timecal.getText()) < rank.item[level].score) {
							String Tip = new String("<html><font face=\"����\">����");
							switch(level) {
							case 0: Tip += "����"; break;
							case 1: Tip += "�м�"; break;
							case 2: Tip += "�߼�"; break;
							default: break;
							}
							Tip += "��¼���������մ���</html>";
							String name = JOptionPane.showInputDialog(frame, Tip, "��ϲ", JOptionPane.PLAIN_MESSAGE);
							if(name != null) {
								rank.item[level].setItem(name, Integer.parseInt(timecal.getText()));
							}
						} else {
							JDialog success = new JDialog(frame, "��Ϸʤ��", true);
							
							JPanel paneltemp = new JPanel(null);
							success.setContentPane(paneltemp);
							
							JLabel labeltemp = new JLabel("��ϲ������" + timecal.getText() + "��ĳɼ������Ϸ");
							labeltemp.setFont(new Font("����", Font.PLAIN, 15));
							labeltemp.setBounds(20, 5, 260, 50);
							paneltemp.add(labeltemp);
							
							JButton buttontemp = new JButton("ȷ��");
							buttontemp.setFont(new Font("����", Font.PLAIN, 11));
							buttontemp.setBounds(100, 60, 60, 30);
							buttontemp.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent e) {
									success.dispose();
								}
								
							});
							paneltemp.add(buttontemp);
							
							success.setSize(labeltemp.getWidth() + 16, labeltemp.getHeight() + buttontemp.getHeight() + 70);
							success.setLocationRelativeTo(null);
							success.setVisible(true);
						}
					}
				} else {
					// ���������
					play = -1;
					timer.stop();
					for(int i = 0; i < row; ++i) {
						for(int j = 0; j < col; ++j) {
							if(mark[i][j] == Status.FAILED.getValue()) {
								if(label[i][j].status == Status.FLAG) {
									label[i][j].setStatus(Status.FAILFLAG);
								} else {
									label[i][j].setStatus(Status.FAILED);
								}
							}
						}
					}
					label[p.x][p.y].setStatus(Status.BOOM);
				}
			}
		}
		// ��Ӹ������
		private void addComponent() {
			// ���ʱ��
			ImageIcon Clock = new ImageIcon("image/CLOCK.png");
			JLabel clock = new JLabel(Clock);
			clock.setBounds(X, Y + DISY + Label.HEIGHT * row, Clock.getIconWidth(), Clock.getIconHeight());
			add(clock);
			
			// ��ӵ������
			ImageIcon Mine = new ImageIcon("image/MINE.png");
			JLabel mine = new JLabel(Mine);
			mine.setBounds(X + Label.WIDTH * col - Mine.getIconWidth(), 
					Y + DISY + Label.HEIGHT * row, Mine.getIconWidth(), Mine.getIconHeight());
			add(mine);

			// ������ֱ���
			ImageIcon Back = new ImageIcon("image/LABEL.png");

			JLayeredPane backjlp1 = new JLayeredPane();
			JLabel back1 = new JLabel(Back);
			back1.setBounds(0, 0, Back.getIconWidth(), Back.getIconHeight());
			backjlp1.add(back1, new Integer(0));
			backjlp1.setBounds(X + DISX + Clock.getIconWidth(), Y + DISY + Label.HEIGHT * row, 
					Back.getIconWidth(), Back.getIconHeight());
			add(backjlp1);
			
			JLayeredPane backjlp2 = new JLayeredPane();
			JLabel back2 = new JLabel(Back);
			back2.setBounds(0, 0, Back.getIconWidth(), Back.getIconHeight());
			backjlp2.add(back2,  new Integer(0));
			backjlp2.setBounds(X - DISX - Mine.getIconWidth() - Back.getIconWidth() + Label.WIDTH * col, 
					Y + DISY + Label.HEIGHT * row, 
					Back.getIconWidth(), Back.getIconHeight());
			add(backjlp2);
			
			// ��Ӽ�ʱ�����ı�
			JPanel cal = new JPanel();
			timecal = new JLabel("0");
			timecal.setForeground(Color.WHITE);
			cal.add(timecal);
			cal.setBounds(0, 0, Back.getIconWidth(), Back.getIconHeight());
			cal.setOpaque(false);
			backjlp1.add(cal, new Integer(1));
			timer = new Timer(1000, new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					int temp = Integer.parseInt(timecal.getText());
					if(temp < 999) {
						++temp;
						timecal.setText(String.valueOf(temp));
					}
				}
			});
			
			// ������׼����ı�
			JPanel cnt = new JPanel();
			minecnt = new JLabel(String.valueOf(mines));
			minecnt.setForeground(Color.WHITE);
			cnt.add(minecnt);
			cnt.setBounds(0, 0, Back.getIconWidth(), Back.getIconHeight());
			cnt.setOpaque(false);
			backjlp2.add(cnt, new Integer(1));
		}
	}
	class Option extends JDialog {
		private static final long serialVersionUID = 1L;
		int select = level;
		public Option(JFrame frame, int s) {
			super(frame, true);
			setSize(200, 165);
			setTitle("ѡ��");
			JPanel ppanel = new JPanel();
			setContentPane(ppanel);
			String[] llevel = {"������9��9   10���ף�", "�м���16��16 40���ף�", "�߼���16��30 99���ף�"};
			ButtonGroup group = new ButtonGroup();
			
			JRadioButton[] radio = new JRadioButton[3];
			for(int i = 0; i < llevel.length; ++i) {
				if(i == select) {
					radio[i] = new JRadioButton(llevel[i], true);
				} else {
					radio[i] = new JRadioButton(llevel[i], false);
				}
				radio[i].setFont(new Font("����", Font.PLAIN, 13));
				group.add(radio[i]);
				ppanel.add(radio[i], BorderLayout.NORTH);
			}
			radio[0].addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					select = 0;
				}
			});
			radio[1].addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					select = 1;
				}
			});
			radio[2].addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					select = 2;
				}
			});
			
			JButton ok = new JButton("ȷ��");
			ok.setFont(new Font("����", Font.PLAIN, 13));
			JButton cancel = new JButton("ȡ��");
			cancel.setFont(new Font("����", Font.PLAIN, 13));
			ppanel.add(ok, BorderLayout.SOUTH);
			ppanel.add(cancel, BorderLayout.SOUTH);
			ok.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					level = select;
					panel.reSize(Difficult.Diff[select][0], Difficult.Diff[select][1], Difficult.Diff[select][2]);
					dispose();
				}
			});
			cancel.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					dispose();
				}
			});

			setLocationRelativeTo(null);
			setVisible(true);
		}
	}
	class Stat extends JDialog {
		private static final long serialVersionUID = 1L;

		public Stat(JFrame frame) {
			super(frame, "���а�", false);
			Font font = new Font("����", Font.PLAIN, 15);
			
			JPanel jp = new JPanel(null);
			setContentPane(jp);
			
			String left = "";
			left += "<html>������" + rank.item[0].score + "��<br>";
			left += "�м���" + rank.item[1].score + "��<br>";
			left += "�߼���" + rank.item[2	].score + "��</br></html>";
			JLabel text1 = new JLabel(left);
			text1.setFont(font);
			text1.setBounds(30, 20, 100, 60);
			jp.add(text1);
			
			String right = "";
			right += "<html>" + rank.item[0].name + "<br>";
			right += rank.item[1].name	+ "<br>";
			right += rank.item[2].name + "</br></html>";
			JLabel text2 = new JLabel(right);
			text2.setFont(font);
			text2.setBounds(170, 20, 100, 60);
			jp.add(text2);
			
			JButton recount = new JButton("���¼Ʒ�");
			recount.setFont(new Font("����", Font.PLAIN, 12));
			recount.setBounds(70, 110, 90, 30);
			recount.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					for(int i = 0; i < 3; ++i) {
						rank.reSet();
						rank.writeFile("Ranking.dat");
						String l = "";
						l += "<html>������" + rank.item[0].score + "��<br>";
						l += "�м���" + rank.item[1].score + "��<br>";
						l += "�߼���" + rank.item[2	].score + "��</br></html>";
						text1.setText(l);
						String r = "";
						r += "<html>" + rank.item[0].name + "<br>";
						r += rank.item[1].name	+ "<br>";
						r += rank.item[2].name + "</br></html>";
						text2.setText(r);
					}
				}
			});
			jp.add(recount);
			
			setSize(250, 200);
			setLocationRelativeTo(null);
			setVisible(true);
		}
	}
	int level;
	Panel panel;
	Rank rank;
	
	public Demine() {
		super("ɨ��");
		
		rank = new Rank("Ranking.dat");
		level = 0;
		setIconImage(new ImageIcon("image/ICON.png").getImage());
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		panel = new Panel(this, Difficult.Diff[level][0], Difficult.Diff[level][1], Difficult.Diff[level][2]);
		setContentPane(panel);

		// Font
		Font font = new Font("����", Font.PLAIN, 13);
		UIManager.put("Menu.font", font);
		UIManager.put("MenuItem.font", font);
		
		// Menu
		JMenuBar bar = new JMenuBar();
		setJMenuBar(bar);
		JMenu game = new JMenu("��Ϸ");
		JMenu help = new JMenu("����");
		bar.add(game);
		bar.add(help);

		// NewGame MenuItem
		JMenuItem newGame = new JMenuItem("����Ϸ");
		newGame.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				panel.reSize(Difficult.Diff[level][0], Difficult.Diff[level][1], Difficult.Diff[level][2]);
			}
		});
		JFrame temp = this;
		JMenuItem stat = new JMenuItem("ͳ��");
		stat.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new Stat(temp);
			}
		});
		JMenuItem option = new JMenuItem("ѡ��");
		option.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new Option(temp, level);
			}
		});
		JMenuItem exit = new JMenuItem("�˳�");
		exit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				rank.writeFile("Ranking.dat");
				System.exit(0);
			}
		});
		game.add(newGame);
		game.addSeparator();
		game.add(stat);
		game.add(option);
		game.addSeparator();
		game.add(exit);
		
		// Help MenuItem
		JMenuItem viewHelp = new JMenuItem("����");
		viewHelp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					Desktop.getDesktop().browse(new URL("http://jingyan.baidu.com/article/7f766daf9231e84101e1d03d.html").toURI());
				} catch (IOException | URISyntaxException e1) {
					JOptionPane.showMessageDialog(null, "�����쳣��������������", "����",
							JOptionPane.WARNING_MESSAGE);
				}
			}
		});
		JMenuItem about = new JMenuItem("����ɨ��");
		about.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					Desktop.getDesktop().browse(new URL("http://baike.baidu.com/item/%E6%89%AB%E9%9B%B7/12543?fr=aladdin").toURI());
				} catch (IOException | URISyntaxException e1) {
					JOptionPane.showMessageDialog(null, "�����쳣��������������", "����",
							JOptionPane.WARNING_MESSAGE);
				}
			}
		});
		JMenuItem getMore = new JMenuItem("������ȡ������Ϸ");
		getMore.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					Desktop.getDesktop().browse(new URL("http://www.4399.com").toURI());
				} catch (IOException | URISyntaxException e1) {
					JOptionPane.showMessageDialog(null, "�����쳣��������������", "����",
							JOptionPane.WARNING_MESSAGE);
				}
			}
		});
		help.add(viewHelp);
		help.addSeparator();
		help.add(about);
		help.addSeparator();
		help.add(getMore);

		setResizable(false);
		ReSize();
		setVisible(true);
		
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				rank.writeFile("Ranking.dat");
			}
		});
	}
	public void ReSize() {
		setSize(Panel.Label.WIDTH * Difficult.Diff[level][1] + 105, 
				Panel.Label.HEIGHT * Difficult.Diff[level][0] + 140);
		setLocationRelativeTo(null);
	}
}

public class Main {
	public static void main(String[] args) {
		Demine d = new Demine();
		d.getExtendedState();
	}
}