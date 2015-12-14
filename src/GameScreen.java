
import java.applet.Applet;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import com.sun.media.jfxmedia.AudioClip;

public class GameScreen extends JPanel {

	private GameLogic logic;
	private final Font font = new Font("Jokerman", Font.BOLD, 30);
	// private final Font smallfont = new Font("Tahoma", Font.PLAIN, 20);
	private BufferedImage classroomBG, table, staticBook,bonusTimeIcon;
	public static java.applet.AudioClip backGroundMusic;
	public static PopUp popup;

	public GameScreen(GameLogic logic) {
		super();
		setDoubleBuffered(true);
		setPreferredSize(new Dimension(720, 480));
		this.logic = logic;
		setVisible(true);
		setFocusable(true);
		requestFocus();
		RenderableHolder.getInstance().getRenderableList().clear();

		ClassLoader loader = Main.class.getClassLoader();
		try {
			classroomBG = ImageIO.read(loader.getResource("res/Classroom.png"));
			table = ImageIO.read(loader.getResource("res/Table.png"));
			staticBook = ImageIO.read(loader.getResource("res/ontable-3.png"));
			bonusTimeIcon = ImageIO.read(loader.getResource("res/BonusTime.png"));
			try {
				backGroundMusic = Applet.newAudioClip((loader.getResource("res/background.wav")).toURI().toURL());
			} catch (URISyntaxException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		RenderableHolder.getInstance().add(logic.getStudent1());
		RenderableHolder.getInstance().add(logic.getStudent2());
		RenderableHolder.getInstance().add(logic.getTeacher());
		RenderableHolder.getInstance().add(logic.getNoodles1());
		RenderableHolder.getInstance().add(logic.getNoodles2());
		addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void keyReleased(KeyEvent e) {

			}

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_A) {
					if (!InputUtility.isaTriggered()) {
						InputUtility.setaTriggered(true);
						InputUtility.setdTriggered(false);
						logic.hitButton(logic.getStudent1(), logic.getTeacher());
					}
				}
				if (e.getKeyCode() == KeyEvent.VK_D) {
					if (!InputUtility.isdTriggered()) {
						InputUtility.setdTriggered(true);
						InputUtility.setaTriggered(false);
						logic.hitButton(logic.getStudent1(), logic.getTeacher());
					}
				}
				if (e.getKeyCode() == KeyEvent.VK_J) {
					if (!InputUtility.isjTriggered()) {
						InputUtility.setjTriggered(true);
						InputUtility.setlTriggered(false);
						logic.hitButton(logic.getStudent2(), logic.getTeacher());
					}
				}
				if (e.getKeyCode() == KeyEvent.VK_L) {
					if (!InputUtility.islTriggered()) {
						InputUtility.setlTriggered(true);
						InputUtility.setjTriggered(false);
						logic.hitButton(logic.getStudent2(), logic.getTeacher());
					}
				}
				if (e.getKeyCode() == KeyEvent.VK_S) {
					logic.getStudent1().setEating(false);
					InputUtility.setaTriggered(false);
					InputUtility.setdTriggered(false);
				}
				if (e.getKeyCode() == KeyEvent.VK_K) {
					logic.getStudent2().setEating(false);
					InputUtility.setaTriggered(false);
					InputUtility.setdTriggered(false);
				}
			}
		});
	}

	public void runGame(int selectedMode) {

		if (selectedMode == 1) {
			logic.update(logic.getStudent1(), logic.getTeacher());
			logic.getStudent2().setEating(false);
			logic.getStudent2().setDecreaseScore(true);

		} else if (selectedMode == 2) {
			logic.update(logic.getStudent2(), logic.getTeacher());
			logic.getStudent1().setEating(false);
			logic.getStudent1().setDecreaseScore(true);

		} else {
			logic.update(logic.getStudent1(), logic.getStudent2(), logic.getTeacher());

		}

	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		g2d.drawImage(classroomBG, null, 0, 0);
		g2d.setColor(Color.white);
		g2d.setFont(font);
		if (Main.selectedMode == 3 || Main.selectedMode == 1) {
			g2d.drawString("Blossom : " + logic.getStudent1().getScore(), 220, 90);
			g2d.setColor(Color.darkGray);
			g2d.drawString("life:" + logic.getStudent1().getLife(), 5, 475);
			g2d.drawString("time:" + logic.getTeacher().getRemainingTime(), 300, 40);
		}
		if (Main.selectedMode == 3 || Main.selectedMode == 2) {
			g2d.drawString("Buttercup : " + logic.getStudent2().getScore(), 220, 150);
			g2d.setColor(Color.darkGray);

			g2d.drawString("life:" + logic.getStudent2().getLife(), 625, 475);
			g2d.drawString("time:" + logic.getTeacher().getRemainingTime(), 300, 40);

		}
		if(Main.logic.isBonusTime()){
			g2d.drawImage(bonusTimeIcon,null,0,0);
		}
		
		
		for (IRenderable x : RenderableHolder.getInstance().getRenderableList()) {
			if (x.getZ() < 3) {
				x.render(g2d);
			} else {
				g2d.drawImage(table, null, 0, 0);
				g2d.drawImage(staticBook, null, 0, 0);
				break;
			}

		}
		for (IRenderable x : RenderableHolder.getInstance().getRenderableList()) {
			if (x.getZ() >= 3) {
				x.render(g2d);
			}

		}
		try{
			synchronized(popup){
				try {
					popup.wait();
					if(popup.isShow()){
						popup.drawPopUp(g2d);
						popup.setShow(false);
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch(NullPointerException e){}

	}
}