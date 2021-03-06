package de.dreierschach.daddel.gfx.sprite;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.dreierschach.daddel.Screen.Debug;
import de.dreierschach.daddel.listener.CollisionListener;
import de.dreierschach.daddel.listener.SpriteMoveFinishedListener;
import de.dreierschach.daddel.model.Pos;
import de.dreierschach.daddel.model.Scr;
import de.dreierschach.daddel.model.SpriteGameLoop;
import de.dreierschach.daddel.model.Transformation;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.transform.Rotate;

/**
 * Basisklasse für alle Arten von Sprites
 * 
 * @author Christian
 */
/**
 * @author Christian
 *
 */
public abstract class Sprite implements Comparable<Sprite> {
	public static final int NO_TYPE = -1;
	public static final Sprite NONE = new Sprite(null, NO_TYPE, -99999) {
		@Override
		public void draw(GraphicsContext g) {
		}
	};
	private Pos pos = new Pos(0, 0);
	private double r;
	private int type;
	private List<SpriteGameLoop> gameLoops = new ArrayList<>();
	private boolean alive = true;
	private long ticks = 0;
	private double rotation = 0;
	private double direction = 0;
	private Transformation transformation;
	private Sprite parent = null;
	private Debug debug = Debug.off;
	private boolean showPosOnDebug = true;
	private CollisionListener collisionListener = (me, other) -> {
	};
	private int layer = 0;
	private boolean layerChanged = false;

	private boolean moving = false;
	private boolean startMoving = false;
	private Pos moveStartPos = new Pos(0, 0);
	private Pos moveEndPos = new Pos(0, 0);
	private long moveStartTime;
	private long moveDeltaTime;
	private SpriteMoveFinishedListener moveFinishedListener = me -> {
	};

	/**
	 * Sprite mit vorgegebenen Radius erzeigen
	 * 
	 * @param transformation
	 *            Informationen zur Umrechnung von Spielraster-Punkten in
	 *            Bildschirmpixel
	 * @param type
	 *            Benutzerdefinierter Typ, Integer
	 * @param layer
	 *            Die Ebene, auf der der Sprite angezeigt wird
	 * @param r
	 *            Radius des Sprite, wird zur Kollisionserkennung verwendet
	 */
	public Sprite(Transformation transformation, int type, int layer, double r) {
		this.r = r;
		this.type = type;
		this.transformation = transformation;
		this.layer = layer;
		this.gameLoops.add(movingGameLoop);
	}

	/**
	 * Sprite mit Radius 1
	 * 
	 * @param transformation
	 *            Informationen zur Umrechnung von Spielraster-Punkten in
	 *            Bildschirmpixel
	 * @param type
	 *            Benutzerdefinierter Typ, Integer
	 * @param layer
	 *            Die Ebene, auf der der Sprite angezeigt wird
	 */
	public Sprite(Transformation transformation, int type, int layer) {
		this(transformation, type, layer, 1);
	}

	/**
	 * Legt fest, ob debug-Informationen angezeigt werden
	 * 
	 * @param debug
	 *            true, wenn debug Informationen angezeigt werden sollen
	 * @return this
	 */
	public Sprite debug(Debug debug) {
		this.debug = debug;
		return this;
	}

	/**
	 * @return true, wenn debug Informationen angezeigt werden sollen
	 */
	public Debug debug() {
		return debug;
	}

	/**
	 * Legt fest, ob im Debug-Modus auch die Position angezeigt wird
	 * 
	 * @param showPosOnDebug
	 *            true: zeige die Position im Debug-Modus
	 * @return this
	 */
	public Sprite showPosOnDebug(boolean showPosOnDebug) {
		this.showPosOnDebug = showPosOnDebug;
		return this;
	}

	/**
	 * @return true: zeige die Position im Debug-Modus
	 */
	public boolean showPosOnDebug() {
		return showPosOnDebug;
	}

	/**
	 * @return aktuelle Position in Spielraster-Punkten, ggf. relativ zum
	 *         Eltern-Sprite
	 */
	public Pos effektivePos() {
		return !hasParent() ? pos : parent.effektivePos().add(pos);
	}

	/**
	 * @return die relative Position in Spielraster-Punkten
	 */
	public Pos pos() {
		return pos;
	}

	/**
	 * Setzt die relative Position in Spielraster-Punkten
	 * 
	 * @param x
	 *            die X-Koordinate
	 * @param y
	 *            die X-Koordinate
	 * @return this
	 */
	public Sprite pos(double x, double y) {
		this.pos(new Pos(x, y));
		return this;
	}

	/**
	 * Setzt die relative Position in Spielraster-Punkten
	 * 
	 * @param pos
	 *            die neue relative Position
	 * @return this
	 */
	public Sprite pos(Pos pos) {
		this.pos = pos;
		return this;
	}

	/**
	 * @return die aktuelle Drehrichtung des Sprite (0 ... 360)
	 */
	public double rotation() {
		return rotation;
	}

	/**
	 * setzt die aktuelle Drehrichtung des Sprite
	 * 
	 * @param rotation
	 *            die neue Drehrichtung (0 .. 360)
	 * @return this
	 */
	public Sprite rotation(double rotation) {
		this.rotation = rotation;
		return this;
	}

	/**
	 * Dreht das Sprite um den angegebenen Winkel
	 * 
	 * @param angle
	 *            der Winkel, um den gedreht werden soll (0 ... 360)
	 * @return this
	 */
	public Sprite rotate(double angle) {
		rotation += angle;
		return this;
	}

	/**
	 * @return die aktuelle Bewegungsrichtung (0 ... 360)
	 */
	public double direction() {
		return direction;
	}

	/**
	 * setzt die aktuelle Bewegungsrichtung (0 ... 360)
	 * 
	 * @param direction
	 *            Winkel in Grad (0 ... 360)
	 * @return this
	 */
	public Sprite direction(double direction) {
		this.direction = direction;
		return this;
	}

	/**
	 * @return der Radius in Spielraster-Punkten
	 */
	public double r() {
		return r;
	}

	/**
	 * setzt den Radius in Spielraster-Punkten. Wird für die Kollisionserkennung
	 * verwendet.
	 * 
	 * @param r
	 *            Radius in Spielraster-Punkten
	 * @return this
	 */
	public Sprite r(double r) {
		this.r = r;
		return this;
	}

	/**
	 * @return der benutzerdefinierte Typ des Sprites
	 */
	public int type() {
		return type;
	}

	/**
	 * setzt den benutzerdefinierte Typ des Sprites
	 * 
	 * @param type
	 *            der benutzerdefinierte Typ des Sprites, Integer
	 * @return this
	 */
	public Sprite type(int type) {
		this.type = type;
		return this;
	}

	/**
	 * prüft, ob dieser Sprite mit dem angegebenen Sprite kollidiert.
	 * Ausschlaggebend sind der Abstand und die Radien der Sprites.
	 * 
	 * @param other
	 *            der andere Sprite
	 * @return true, wenn eine Kollision vorliegt
	 */
	public boolean collides(Sprite other) {
		if (!other.alive()) {
			return false;
		}
		
		double dd = this.pos().squareDistance(other.pos());
		double dr = other.r + this.r;
		double ddr = dr * dr;
		return dd < ddr;
	}

	/**
	 * wird im Falle eine Kollision aufgerufen; der Kollisions-Listener des
	 * angegebenen Sprites wird aufgerufen
	 * 
	 * @param other
	 *            der Sprite, mit dem die Kollision geschehen ist
	 */
	public void onCollision(Sprite other) {
		this.collisionListener.onCollision(this, other);
	}

	/**
	 * @return true, wenn der Sprite noch lebt
	 */
	public boolean alive() {
		return alive;
	}

	/**
	 * tötet den Sprite. Bei nächster Gelegenheit wird er aus der View-Hierarchie
	 * entfernt
	 */
	public void kill() {
		this.alive = false;
	}
	
	public void moveTo(Pos endPos, double speed, SpriteMoveFinishedListener moveFinishedListener) {
		moveTo(endPos, (long)(1000d / speed), moveFinishedListener);
	}
	
	public void moveTo(Pos endPos, long deltaTime, SpriteMoveFinishedListener moveFinishedListener) {
		this.moveStartPos = this.pos;
		this.moveEndPos = endPos;
		this.moveDeltaTime = deltaTime;
		this.moveFinishedListener = moveFinishedListener;
		this.startMoving = true;
	}

	/**
	 * Fügt die angegebenen Aktionen zur Spielschleife des Sprite hinzu
	 * 
	 * @param gameLoops
	 *            die benutzerdefinierten Aktionen
	 * 
	 * @return this
	 */
	public Sprite gameLoop(SpriteGameLoop... gameLoops) {
		this.gameLoops.addAll(Arrays.asList(gameLoops));
		return this;
	}

	/**
	 * Wird intern ausgeführt
	 * 
	 * @param deltatime
	 *            die Zeitspanne seit dem letzten Aufruf in ms
	 */
	public void gameLoop(long deltatime) {
		for (SpriteGameLoop gameLoop : gameLoops) {
			gameLoop.run(this, ticks, deltatime);
		}
		ticks += deltatime;
	}

	/**
	 * @return die Informationen, um Bildschirmpixel in Spielraster-Punkten
	 *         umzurechnen
	 */
	public Transformation transformation() {
		return transformation;
	}

	/**
	 * Gibt den Eltern-Sprite zurück, null falls es keinen gibt.
	 * 
	 * @return den Eltern-Sprite
	 */
	public Sprite parent() {
		return parent;
	}

	/**
	 * Setzt den Eltern-Sprite; die effektive Position ist nun relativ zu diesem
	 * Sprite
	 * 
	 * @param parent
	 *            der neue Elternsprite
	 * @return this
	 */
	public Sprite parent(Sprite parent) {
		this.parent = parent;
		return this;
	}

	/**
	 * @return true, falls es einen Eltern-Sprite gibt
	 */
	public boolean hasParent() {
		return parent != null;
	}

	/**
	 * setzt die Aktion, die bei einer Kollision mit anderen Sprites ausgeführt
	 * werden soll
	 * 
	 * @param collisionListener
	 *            die Aktion bei Kollisionen
	 * @return this
	 */
	public Sprite collision(CollisionListener collisionListener) {
		this.collisionListener = collisionListener;
		return this;
	}

	/**
	 * Bewegt den Sprite um den in Pos angegebenen Vektor
	 * 
	 * @param direction
	 *            der Bewegungsvektor
	 * @return this
	 */
	public Sprite move(Pos direction) {
		this.pos = new Pos(pos.x() + direction.x(), pos.y() + direction.y());
		return this;
	}

	/**
	 * Bewegt den Sprite um die angegebene Strecke in die aktuelle Richtung
	 * 
	 * @param distance
	 *            die Strecke in Spielraster-Punkten
	 * @return this
	 */
	public Sprite move(double distance) {
		Rotate r = new Rotate(direction);
		Point2D v2d = r.transform(new Point2D(1, 0));
		Pos v = new Pos((double) v2d.getX(), (double) v2d.getY());
		this.pos = this.pos.add(v.mul(distance));
		return this;
	}

	/**
	 * gibt die Bildschirmebene des Sprite zurück
	 * 
	 * @return die Nummer der Bildschirmebene (kleinere Ebene = weiter hinten,
	 *         größere = weiter vorne)
	 */
	public int layer() {
		return layer;
	}

	/**
	 * setzt die Bildschirmebene, auf der der Sprite angezeigt wird
	 * 
	 * @param layer
	 *            die Nummer der Bildschirmebene (kleinere Ebene = weiter hinten,
	 *            größere = weiter vorne)
	 * 
	 * @return this
	 */
	public Sprite layer(int layer) {
		this.layer = layer;
		this.layerChanged = true;
		return this;
	}

	// ------------- interne methoden

	public boolean layerChanged() {
		return layerChanged;
	}

	public void clrLayerChanged() {
		this.layerChanged = false;
	}

	protected static void rotate(GraphicsContext g, double angle, Scr middle) {
		Rotate r = new Rotate(angle, middle.x(), middle.y());
		g.setTransform(r.getMxx(), r.getMyx(), r.getMxy(), r.getMyy(), r.getTx(), r.getTy());
	}

	@Override
	public int compareTo(Sprite other) {
		return other.layer > this.layer ? -1 : 1;
	}

	protected long getTicks() {
		return ticks;
	}

	protected void setTicks(long ticks) {
		this.ticks = ticks;
	}

	public void drawSprite(GraphicsContext g) {
		rotate(g, rotation, transformation.t(effektivePos()));
		draw(g);
	}

	public abstract void draw(GraphicsContext g);

	private SpriteGameLoop movingGameLoop = (sprite, total, delta) -> {
		if (startMoving) {
			moveStartTime = total;
			startMoving = false;
			moving = true;
		}
		if (moving) {
			long endTime = moveStartTime + moveDeltaTime;
			if (total >= endTime) {
				moving = false;
				this.pos = moveEndPos;
				moveFinishedListener.onDestinationReached(this);
			} else {
				double d = ((double) (total - moveStartTime)) / (double) (endTime - moveStartTime);
				Pos p = new Pos(d * (moveEndPos.x() - moveStartPos.x()) + moveStartPos.x(),
						d * (moveEndPos.y() - moveStartPos.y()) + moveStartPos.y());
				sprite.pos(p);
			}
		}
	};
}
