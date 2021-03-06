package de.dreierschach.pacman;

import java.util.Random;

import de.dreierschach.daddel.Daddel;
import de.dreierschach.daddel.gfx.Gfx;
import de.dreierschach.daddel.gfx.tilemap.Entity;
import de.dreierschach.daddel.gfx.tilemap.Entity.Dir;
import de.dreierschach.daddel.gfx.tilemap.TileMap;
import de.dreierschach.daddel.listener.SpriteMoveFinishedListener;
import de.dreierschach.daddel.model.MapPos;
import de.dreierschach.daddel.model.Pos;
import de.dreierschach.daddel.validator.Validator;
import javafx.scene.input.KeyCode;

public class Pacman extends Daddel {

	private static int TYPE_WALL = 1;
	private static int TYPE_GATE = 2;
	private static int TYPE_PACMAN = 3;
	private static int TYPE_PILLE = 4;
	private static int TYPE_PILLE_GROSS = 5;
	private static int TYPE_GHOST = 6;

	private TileMap map;
	private static final int ID_WALL_ROUND_RU = (int) 'a';
	private static final int ID_WALL_ROUND_LU = (int) 'b';
	private static final int ID_WALL_ROUND_RO = (int) 'c';
	private static final int ID_WALL_ROUND_LO = (int) 'd';
	private static final int ID_WALL_RU = (int) 'A';
	private static final int ID_WALL_LU = (int) 'B';
	private static final int ID_WALL_RO = (int) 'C';
	private static final int ID_WALL_LO = (int) 'D';
	private static final int ID_WALL_H = (int) '-';
	private static final int ID_WALL_V = (int) '|';
	private static final int ID_WALL_GATE = (int) '=';
	private static final int ID_PILLE = (int) '.';
	private static final int ID_GROSSE_PILLE = (int) 'o';
	private static final int ID_DEATEYES_D = (int) ':';

	private static final float PACMAN_SPEED = 8f;
	private static final float PACMAN_ANIMATION_SPEED = 3f;
	private static final float GHOST_SPEED = 8f;
	private static final float GHOST_WHITE_SPEED = 6f;
	private static final float GHOST_BLUE_SPEED = 6f;
	private static final float GHOST_ANIMATION_SPEED = 3f;

	private final MapPos[] homeGhosts = { //
			new MapPos(12, 14, 0), new MapPos(17, 14, 0), new MapPos(12, 16, 0), new MapPos(17, 16, 0) };

	private MapPos pacmanLevelPos;

	private static final String[][] GFX_GHOSTS = { //
			{ //
					Gfx.PAC_BASHFUL_INKY_L0, Gfx.PAC_BASHFUL_INKY_L1, //
					Gfx.PAC_BASHFUL_INKY_U0, Gfx.PAC_BASHFUL_INKY_U1, //
					Gfx.PAC_BASHFUL_INKY_R0, Gfx.PAC_BASHFUL_INKY_R1, //
					Gfx.PAC_BASHFUL_INKY_D0, Gfx.PAC_BASHFUL_INKY_D1, //
					Gfx.PAC_GHOST_BLUE0, Gfx.PAC_GHOST_BLUE1, //
					Gfx.PAC_GHOST_WHITE0, Gfx.PAC_GHOST_WHITE1, //
					Gfx.PAC_DEADEYES_L, //
					Gfx.PAC_DEADEYES_U, //
					Gfx.PAC_DEADEYES_R, //
					Gfx.PAC_DEADEYES_D //
			}, { //
					Gfx.PAC_POKEY_CLYDE_L0, Gfx.PAC_POKEY_CLYDE_L1, //
					Gfx.PAC_POKEY_CLYDE_U0, Gfx.PAC_POKEY_CLYDE_U1, //
					Gfx.PAC_POKEY_CLYDE_R0, Gfx.PAC_POKEY_CLYDE_R1, //
					Gfx.PAC_POKEY_CLYDE_D0, Gfx.PAC_POKEY_CLYDE_D1, //
					Gfx.PAC_GHOST_BLUE0, Gfx.PAC_GHOST_BLUE1, //
					Gfx.PAC_GHOST_WHITE0, Gfx.PAC_GHOST_WHITE1, //
					Gfx.PAC_DEADEYES_L, //
					Gfx.PAC_DEADEYES_U, //
					Gfx.PAC_DEADEYES_R, //
					Gfx.PAC_DEADEYES_D //
			}, { //
					Gfx.PAC_SHADOW_BLINKY_L0, Gfx.PAC_SHADOW_BLINKY_L1, //
					Gfx.PAC_SHADOW_BLINKY_U0, Gfx.PAC_SHADOW_BLINKY_U1, //
					Gfx.PAC_SHADOW_BLINKY_R0, Gfx.PAC_SHADOW_BLINKY_R1, //
					Gfx.PAC_SHADOW_BLINKY_D0, Gfx.PAC_SHADOW_BLINKY_D1, //
					Gfx.PAC_GHOST_BLUE0, Gfx.PAC_GHOST_BLUE1, //
					Gfx.PAC_GHOST_WHITE0, Gfx.PAC_GHOST_WHITE1, //
					Gfx.PAC_DEADEYES_L, //
					Gfx.PAC_DEADEYES_U, //
					Gfx.PAC_DEADEYES_R, //
					Gfx.PAC_DEADEYES_D //
			}, { //
					Gfx.PAC_SPEEDY_PINKY_L0, Gfx.PAC_SPEEDY_PINKY_L1, //
					Gfx.PAC_SPEEDY_PINKY_U0, Gfx.PAC_SPEEDY_PINKY_U1, //
					Gfx.PAC_SPEEDY_PINKY_R0, Gfx.PAC_SPEEDY_PINKY_R1, //
					Gfx.PAC_SPEEDY_PINKY_D0, Gfx.PAC_SPEEDY_PINKY_D1, //
					Gfx.PAC_GHOST_BLUE0, Gfx.PAC_GHOST_BLUE1, //
					Gfx.PAC_GHOST_WHITE0, Gfx.PAC_GHOST_WHITE1, //
					Gfx.PAC_DEADEYES_L, //
					Gfx.PAC_DEADEYES_U, //
					Gfx.PAC_DEADEYES_R, //
					Gfx.PAC_DEADEYES_D //
			} };

	private Random random = new Random();

	private static final String[][][] LEVEL = {
			// Level 1
			{ { //
					"A------------B  A------------B", //
					"|a-----------d..c-----------b|", //
					"||..........................||", //
					"||.a--b.a---b.ab.a---b.a--b.||", //
					"||o|  |.|   |.||.|   |.|  |o||", //
					"||.c--d.c---d.||.c---d.c--d.||", //
					"||............||............||", //
					"||.a--b.ab.a--dc--b.ab.a--b.||", //
					"||.c--d.||.c--ba--d.||.c--d.||", //
					"||......||....||....||......||", //
					"|c----b.|c--b.||.a--d|.a----d|", //
					"|     |.|a--d.cd.c--b|.|     |", //
					"|     |.||..........||.|     |", //
					"|     |.||.A--==--B.||.|     |", //
					"C-----d.cd.|*    *|.cd.c-----D", //
					" ..........|      |.......... ", //
					"A-----b.ab.|*    *|.ab.a-----B", //
					"|     |.||.C------D.||.|     |", //
					"|     |.||....#.....||.|     |", //
					"|     |.||.a------b.||.|     |", //
					"|a----d.cd.c--ba--d.cd.c----b|", //
					"||............||............||", //
					"||.a--b.a---b.||.a---b.a--b.||", //
					"||.c-b|.c---d.cd.c---d.|a-d.||", //
					"||o..||................||..o||", //
					"|c-b.||.ab.a------b.ab.||.a-d|", //
					"|a-d.cd.||.c--ba--d.||.cd.c-b|", //
					"||......||....||....||......||", //
					"||.a----dc--b.||.a--dc----b.||", //
					"||.c--------d.cd.c--------d.||", //
					"||..........................||", //
					"|c-----------b..a-----------d|", //
					"C------------D  C------------D", } } //
	};

	class MoveState {
		Dir dir = Dir.STOP;
		Dir nextDir = Dir.STOP;
	}

	private MoveState pacmanState = new MoveState();
	private Entity pacman;
	private Entity[] ghosts = new Entity[4];

	@Override
	public void initGame() {
		// toTitle(() -> toLevel());
		toLevel(() -> startLevel());
	}

	// =================== level starten ==

	private void startLevel() {

		initMap();
		initPacman();
		initGhosts();

		// keys

		key(KeyCode.ESCAPE, (keyCode) -> exit());

		key(KeyCode.LEFT, (keyCode) -> packmanGo(Dir.LEFT));
		key(KeyCode.UP, (keyCode) -> packmanGo(Dir.UP));
		key(KeyCode.DOWN, (keyCode) -> packmanGo(Dir.DOWN));
		key(KeyCode.RIGHT, (keyCode) -> packmanGo(Dir.RIGHT));
		key(KeyCode.SPACE, (keyCode) -> packmanGo(Dir.STOP));
	}

	// -------------- map initialisieren --

	private void initMap() {
		grid(-16, 16, -9f, 9f);
		erzeugeSchraegScrollendeSterne();
		map = tilemap(1f).tile(ID_WALL_ROUND_LO, TYPE_WALL, Gfx.PAC_WALL_ROUND_LO) //
				.tile(ID_WALL_ROUND_RO, TYPE_WALL, Gfx.PAC_WALL_ROUND_RO) //
				.tile(ID_WALL_ROUND_LU, TYPE_WALL, Gfx.PAC_WALL_ROUND_LU) //
				.tile(ID_WALL_ROUND_RU, TYPE_WALL, Gfx.PAC_WALL_ROUND_RU)//
				.tile(ID_WALL_LO, TYPE_WALL, Gfx.PAC_WALL_LO) //
				.tile(ID_WALL_RO, TYPE_WALL, Gfx.PAC_WALL_RO) //
				.tile(ID_WALL_LU, TYPE_WALL, Gfx.PAC_WALL_LU) //
				.tile(ID_WALL_RU, TYPE_WALL, Gfx.PAC_WALL_RU) //
				.tile(ID_WALL_H, TYPE_WALL, Gfx.PAC_WALL_H) //
				.tile(ID_WALL_V, TYPE_WALL, Gfx.PAC_WALL_V) //
				.tile(ID_WALL_GATE, TYPE_GATE, Gfx.PAC_WALL_GATE) //
				.tile(ID_PILLE, TYPE_PILLE, Gfx.PAC_PILLE_KLEIN) //
				.tile(ID_GROSSE_PILLE, TYPE_PILLE_GROSS, Gfx.PAC_PILLE_GROSS) //
				.tile(ID_DEATEYES_D, TYPE_WALL, Gfx.PAC_DEADEYES_D, Gfx.PAC_DEADEYES_R, Gfx.PAC_DEADEYES_U,
						Gfx.PAC_DEADEYES_L)
				.pos(0, 0) //
				// .defaultTile(TileMap.NO_ID)
				.initMap(LEVEL[level() - 1]);

		map.tile(map.defaultTile()).gameLoop(animation(0, 3, false, 2f));
	}
	// ------------- schräg-scrollende Sterne erzeugen --

	public void erzeugeSchraegScrollendeSterne() {
		particleSwarmBuilder(200, -1, Gfx.STERN).initialPosRange(new Pos(-16, -10), new Pos(16, 10))
				.sizeRange(0.02f, 0.4f, 4).direction(30).speedRange(1f, 5f).outsideGrid(PARTICLE_REAPPEAR).create();
	}

	private void initPacman() {
		String[] level = LEVEL[level() - 1][0];

		pacmanLevelPos = new MapPos(2, 2, 0);
		for (int y = 0; y < level.length; y++) {
			for (int x = 0; x < level[y].length(); x++) {
				if (level[y].charAt(x) == '#') {
					pacmanLevelPos = new MapPos(x, y, 0);
				}
			}
		}

		pacman = entity(TYPE_PACMAN, 2f, Gfx.PAC_PACMAN_L3, Gfx.PAC_PACMAN_L0, Gfx.PAC_PACMAN_L1, Gfx.PAC_PACMAN_L2)
				.mapPos(pacmanLevelPos).rotate(180).moveSpeed(PACMAN_SPEED).r(0.8);
		pacman.animation().imageStart(0).imageEnd(0).speed(PACMAN_ANIMATION_SPEED).bounce(false);
		map.focus(pacman);
		pacman.onFinishMove(me -> pacmanGo());

		pacman.collision((me, other) -> {
			if (other.type() != TYPE_GHOST) {
				return;
			}
			pacman.mapPos(pacmanLevelPos);
			pacman.destMapPos(pacmanLevelPos);
			pacmanState.dir = Dir.STOP;
			pacmanState.nextDir = Dir.STOP;
			pacman.rotation(180);
		});
	}

	private void packmanGo(Dir nextDir) {
		pacmanState.nextDir = nextDir;
		if (pacmanState.dir == Dir.STOP) {
			pacmanGo();
		}
	}

	private final Validator<Integer> isBlocked = type -> type == TYPE_WALL || type == TYPE_GATE;
	private final Validator<Integer> isNotBlocked = intExpression().not(isBlocked).create();

	private void pacmanGo() {

		// eat

		if (pacman.checkType(type -> type == TYPE_PILLE || type == TYPE_PILLE_GROSS)) {
			pacman.take();
		}

		// check if it is outside, then return opposite of it

		if (pacman.mapPos().x() == 0) {
			pacman.mapPos(new MapPos(map.size().x() - 1, pacman.mapPos().y(), pacman.mapPos().z()));
		} else if (pacman.mapPos().x() == map.size().x() - 1) {
			pacman.mapPos(new MapPos(0, pacman.mapPos().y(), pacman.mapPos().z()));
		}

		if (pacman.mapPos().y() == 0) {
			pacman.mapPos(new MapPos(pacman.mapPos().x(), map.size().y() - 1, pacman.mapPos().z()));
		} else if (pacman.mapPos().y() == map.size().y() - 1) {
			pacman.mapPos(new MapPos(pacman.mapPos().x(), 0, pacman.mapPos().z()));
		}

		// check move:

		if (pacman.checkType(pacmanState.nextDir, isBlocked)) {
			if (pacman.checkType(pacmanState.dir, isBlocked)) {
				pacmanState.dir = Dir.STOP;
				pacmanState.nextDir = Dir.STOP;
			}
		} else {
			pacmanState.dir = pacmanState.nextDir;
		}

		// animation:

		if (pacmanState.dir == Dir.STOP) {
			pacman.animation().imageEnd(0);
		} else {
			pacman.rotation(pacmanState.dir.rotation()).animation().imageEnd(3);
		}

		// move:

		pacman.move(pacmanState.dir);
	}

	private void initGhosts() {
		String[] level = LEVEL[level() - 1][0];
		int n = 0;

		for (int y = 0; y < level.length; y++) {
			for (int x = 0; x < level[y].length(); x++) {
				if (level[y].charAt(x) == '*') {
					homeGhosts[n] = new MapPos(x, y, 0);
					n++;
				}
			}
		}

		for (int i = 0; i < 4; i++) {
			ghosts[i] = entity(TYPE_GHOST, 2f, //
					GFX_GHOSTS[i]).mapPos(homeGhosts[i]).moveSpeed(GHOST_SPEED).onFinishMove(ghostMoveFinischedListener)
							.r(0.8);
			ghosts[i].animation().speed(GHOST_ANIMATION_SPEED);
			// do first move
			// TODO: automate
			ghostMoveFinischedListener.onDestinationReached(ghosts[i]);
		}
	}

	private final SpriteMoveFinishedListener ghostMoveFinischedListener = me -> {
		Entity ghost = (Entity) me; 
		Dir dir = ghost.lastMove();
		if (ghost.checkType(Dir.UP, type -> type == TYPE_GATE)) {
			dir = Dir.UP;
		} else if (dir == Dir.STOP || ghost.checkType(ghost.lastMove(), isBlocked)
				|| ((ghost.checkType(ghost.lastMove().left(), isNotBlocked)
						|| ghost.checkType(ghost.lastMove().right(), isNotBlocked)) && random.nextBoolean())) {
			int count = 0;
			if (dir == Dir.STOP) {
				dir = Dir.UP;
			}
			if (random.nextBoolean()) {
				dir = dir.left();
			} else {
				dir = dir.right();
			}
			while (count < 4 && ghost.checkType(dir, type -> type == TYPE_WALL || type == TYPE_GATE)) {
				count++;
				dir = dir.left();
			}
		}
		MapPos newPos = ghost.mapPos().add(dir.p());
		if (!tileMap().isValidPosition(newPos)) {
			dir = Dir.STOP;
		}
		ghost.move(dir);
		if (dir != Dir.STOP) {
			int o = dir.ordinal();
			ghost.animation().imageStart(o - 1).imageEnd(o).bounce(false).speed(GHOST_ANIMATION_SPEED);
		}
	};

	@Override
	public void gameLoop(long gesamtZeit, long deltaZeit) {
	}

	// ===================== main-Methode, um das Programm zu starten =========

	public static void main(String[] args) {
		launch(args);
	}
}
