package com.android.Rails;

import java.util.ArrayList;
import java.util.List;

import android.view.animation.DecelerateInterpolator;

enum Mode {
	BUILD, SWITCH
}

enum Scaler {
	OUT, IN, INCR, DECR;

	static Scaler state = OUT;
	static float currentScale = 0;
	static float outScale = 0;
	static float inScale = 0;
	static long animStartTime;

	static DecelerateInterpolator a = new DecelerateInterpolator();

	static boolean process() {

		if (state != Scaler.INCR && state != Scaler.DECR)
			return false;

		float startScale = state == INCR ? outScale : inScale, endScale = state == INCR ? inScale
				: outScale, progress = (RefresherThread.time - animStartTime)
				/ (float) Cnt.zoomAnimDuration;

		if (progress > 1) {
			if (state == Scaler.INCR)
				state = Scaler.IN;
			else
				state = Scaler.OUT;

			currentScale = endScale;
			return true;
		}

		currentScale = startScale + a.getInterpolation(progress)
				* (endScale - startScale);

		return true;
	}

}

public class CoreLogics {

	static float[] screenSize = new float[2];

	static Tile[][] tiles = new Tile[Cnt.cellDim[0]][Cnt.cellDim[1]];
	static float[] tilesSize = { CoreLogics.tiles[0].length * Cnt.cellSize,
			CoreLogics.tiles.length * Cnt.cellSize };

	static int[] selectedTile = { -1, -1 };
	static Mode mode = Mode.BUILD;
	static boolean browseMode = false;
	static Train a;
	static Swarm b;
	static boolean paused = false;

	public static void progress() {

		if (!paused) {
			a.progress();
			b.progress();
		}

		if (Scaler.process())
			RailView.redrawAllReq();

	}

	public static void init() {
		for (int i = 0; i < tiles.length; i++)
			for (int j = 0; j < tiles[0].length; j++)
				if (tiles[i][j] == null)
					tiles[i][j] = new Tile();
				else
					tiles[i][j].reset();

		CoreLogics.addPath(4, 6, "2R 4B R 2B 7L 6T R 3T 2R 3B 2R");
		CoreLogics.addPath(10, 7, "2L 2T 5L T");
		CoreLogics.addPath(8, 4, "L B 3L T");

		a = new Train(9, 4, 6, Lrtb.R);
		b = new Swarm();
		ShotCollector.init();

		paused = false;

	}

	public static float realFromRelative(float in) {
		return Cnt.cellSize * in / Cnt.relativeCellSize;
	}

	public static void addPath(int startX, int startY, String code) {

		List<Lrtb> path = new ArrayList<Lrtb>();

		for (int i = 0; i < code.length(); i++) {
			if (Character.isDigit(code.charAt(i))) {

				Lrtb enumDir = Lrtb.valueOf(code.charAt(i + 1));
				int count = Integer.valueOf(String.valueOf(code.charAt(i)));

				for (int j = 0; j < count; j++) {
					path.add(enumDir);
				}

				i++;

			} else if (Character.isLetter(code.charAt(i))) {
				Lrtb enumDir = Lrtb.valueOf(code.charAt(i));
				path.add(enumDir);
			}
		}

		int[] curCrds = { startX, startY };

		for (int i = 0; i < path.size() - 1; i++) {

			if (curCrds[0] < Cnt.cellDim[0] && curCrds[0] > -1
					&& curCrds[1] < Cnt.cellDim[1] && curCrds[1] > -1) {

				tiles[curCrds[0]][curCrds[1]].getRail().lay(
						Ph.make(path.get(i).inverse(), path.get(i + 1)));
			}

			if (path.get(i + 1) == Lrtb.B)
				curCrds[0]++;
			else if (path.get(i + 1) == Lrtb.T)
				curCrds[0]--;
			else if (path.get(i + 1) == Lrtb.L)
				curCrds[1]--;
			else if (path.get(i + 1) == Lrtb.R)
				curCrds[1]++;

		}
	}

	static public void selectTile(int i, int j) {
		selectedTile[0] = i;
		selectedTile[1] = j;
		tiles[i][j].highlighted = true;
	}

	static public void unselectTile() {
		if (selectedTile[0] == -1)
			return;
		tiles[selectedTile[0]][selectedTile[1]].highlighted = false;
		selectedTile[0] = -1;
		selectedTile[1] = -1;
	}

	/** лежит ли картовая координата
		в указанной ячейке сетки,
		с некоторой погрешностью **/
	static public boolean inBound(float[] in, int[] grid) {

		float[] cellCntr = { grid[1] * Cnt.cellSize + Cnt.cellSize / 2,
				grid[0] * Cnt.cellSize + Cnt.cellSize / 2 };

		if (Math.abs(in[0] - cellCntr[0]) > Cnt.cellSize * 0.7
				|| Math.abs(in[1] - cellCntr[1]) > Cnt.cellSize * 0.7) {
			return false;
		}

		return true;
	}

	/** определяет тип дороги на основании
     	места касания тайла **/
	static public Ph sector(float[] in, int[] grid) {

		float[] gridLUCrnr = { grid[1] * Cnt.cellSize, grid[0] * Cnt.cellSize };
		float[] diff = { in[0] - gridLUCrnr[0], in[1] - gridLUCrnr[1] };

		if (diff[0] < Cnt.cellSize / 3) {
			if (diff[1] < Cnt.cellSize / 3) {
				return Ph.LEFTO;
			} else if (diff[1] < Cnt.cellSize * 2 / 3) {
				return Ph.HOR;
			} else {
				return Ph.LEFBO;
			}
		} else if (diff[0] < Cnt.cellSize * 2 / 3) {
			if (diff[1] < Cnt.cellSize / 3) {
				return Ph.VER;
			} else if (diff[1] < Cnt.cellSize * 2 / 3) {

				if ((diff[1] > diff[0] && diff[1] < Cnt.cellSize - diff[0])
						|| (diff[1] < diff[0] && diff[1] > Cnt.cellSize
								- diff[0]))
					return Ph.HOR;

			} else {
				return Ph.VER;
			}
		} else {
			if (diff[1] < Cnt.cellSize / 3) {
				return Ph.TOPRI;
			} else if (diff[1] < Cnt.cellSize * 2 / 3) {
				return Ph.HOR;
			} else {
				return Ph.BOTRI;
			}
		}

		return Ph.BOTRI;
	}

	static public int mapToGrid(float map) {
		return (int) Math.round(Math.floor(map / Cnt.cellSize));
	}

	static public int[] mapToGrid(float[] map) {
		int[] ret = { mapToGrid(map[1]), mapToGrid(map[0]) };
		return ret;
	}

	static public float[] screenToMap(float[] in) {
		float[] mapPos = new float[2];
		RailView.invMat.mapPoints(mapPos, in);
		return mapPos;
	}

	static public Tile getTile(int i, int j) {

		if (i < 0 || j < 0 || i > tiles.length || j > tiles[0].length)
			return null;

		return tiles[i][j];
	}

}
