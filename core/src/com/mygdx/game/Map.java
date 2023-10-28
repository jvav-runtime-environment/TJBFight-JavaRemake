package com.mygdx.game;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.graphics.Pixmap.Format;

public class Map extends Actor {
    Texture groundImage;
    Vector2 drawMapPosition = new Vector2();
    boolean isReseted = true;
    int[][] map = {
            { 1, 1, 1, 1, 1, 0, 0, 0, 0 },
            { 1, 1, 1, 1, 1, 1, 0, 0, 0 },
            { 1, 1, 1, 1, 1, 1, 1, 0, 0 },
            { 1, 1, 1, 1, 1, 1, 1, 1, 0 },
            { 1, 1, 1, 1, 1, 1, 1, 1, 1 },
            { 0, 1, 1, 1, 1, 1, 1, 1, 1 },
            { 0, 0, 1, 1, 1, 1, 1, 1, 1 },
            { 0, 0, 0, 1, 1, 1, 1, 1, 1 },
            { 0, 0, 0, 0, 1, 1, 1, 1, 1 } };

    /*
     * map的坐标系
     * +——————————————————————>Y
     * |
     * |
     * |
     * |
     * |
     * |
     * ↓
     * X
     * 
     */

    // 临时向量
    private static Vector2 tempVec = new Vector2();

    public static Vector2 getAbsPosition(float x, float y) {
        float outx = MathUtils.cosDeg(120) * y + x;
        float outy = MathUtils.sinDeg(120) * y * 0.6f;

        tempVec.set(outx * Consts.BlockSize, outy * Consts.BlockSize);

        return tempVec.cpy();
    }

    private static void absPositionforDistance(float x, float y) {
        float outx = MathUtils.cosDeg(120) * y + x;
        float outy = MathUtils.sinDeg(120) * y;

        tempVec.set(outx, outy);
    }

    public static float getDistance(float x1, float y1, float x2, float y2) {
        float X1, X2, Y1, Y2;

        absPositionforDistance(x1, y1);
        X1 = tempVec.x;
        Y1 = tempVec.y;
        absPositionforDistance(x2, y2);
        X2 = tempVec.x;
        Y2 = tempVec.y;

        return (float) (Math.sqrt(Math.pow(X1 - X2, 2) + Math.pow(Y1 - Y2, 2)) - 0.001);

    }

    // 反向转换
    private static void calcRelativePosition(float x, float y) {
        x /= Consts.BlockSize;
        y /= Consts.BlockSize;

        float outy = y / (MathUtils.sinDeg(120) * 0.6f);
        float outx = x - MathUtils.cosDeg(120) * outy;

        tempVec.set(outx, outy);
    }

    public static Vector2 getRelativePosition(float x, float y) {
        // 坐标转换
        calcRelativePosition(x, y);

        // 检查位置是否在指定范围
        if ((tempVec.x % 1 >= 0.8f || tempVec.x % 1 <= 0.2f) && (tempVec.y % 1 >= 0.75f || tempVec.y % 1 <= 0.25f)) {
            tempVec.set(Math.round(tempVec.x), Math.round(tempVec.y));
            return tempVec.cpy();
        } else {
            return null;
        }
    }

    Map() {
        Pixmap surface = new Pixmap(25, 25, Format.RGBA8888);
        surface.setColor(1, 1, 1, 1);
        surface.fillCircle(12, 12, 12);

        groundImage = new Texture(surface);
    }

    private boolean testPointAvalible(float x, float y) {
        int x1 = (int) x;
        int y1 = (int) y;
        return 0 <= x1 && x1 <= map.length - 1 && 0 <= y1 && y1 <= map.length - 1;
    }

    public boolean testPointReachable(float x, float y) {
        return testPointAvalible(x, y) && getPoint(x, y) != 0;
    }

    public int getPoint(float x, float y) {
        int x1 = (int) x;
        int y1 = (int) y;

        if (testPointAvalible(x1, y1)) {
            return map[x1][y1];
        }
        return 0;
    }

    public Boolean testPointHasFigure(float x, float y) {
        Array<Figure> figures = Consts.mainstage.selectFigure(new FigureSelector(x, y) {
            public boolean select(Figure figure) {
                return figure.RelativePosition.x == x && figure.RelativePosition.y == y;
            }
        });

        return figures.size != 0;
    }

    public Array<int[]> getFreePointAround(float x, float y, int range) {
        Array<int[]> array = getInRange(x, y, range);
        Array<int[]> occupied = new Array<>();

        for (int[] i : array) {
            if (testPointHasFigure(i[0], i[1])) {
                occupied.add(i);
            }
        }

        array.removeAll(occupied, false);

        return array;
    }

    public void setPoint(Vector2 vec, int status) {
        setPoint((int) vec.x, (int) vec.y, status);
    }

    public void setPoint(float x, float y, int status) {
        int x1 = (int) x;
        int y1 = (int) y;

        if (getPoint(x, y) != 0 && testPointAvalible(x, y)) {
            map[x1][y1] = status;
        }
        isReseted = false;
    }

    public void resetAll() {
        // 重置所有地图块
        int indexX = 0;
        int indexY = 0;

        if (!isReseted) {
            for (int[] i : map) {
                indexY = 0;
                for (int j : i) {
                    if (j != 0) {
                        setPoint(indexX, indexY, 1);
                    }
                    indexY++;
                }
                indexX++;
            }
        }
        isReseted = true;
    }

    private void setRange(int x, int y, int range, int status) {
        for (int[] i : getInRange(x, y, range)) {
            setPoint(i[0], i[1], status);
        }
    }

    public Array<int[]> getInRange(float x, float y, int minrange, int maxrange) {
        Array<int[]> array = getInRange(x, y, maxrange);

        for (int[] i : getInRange(x, y, minrange)) {
            array.removeValue(i, false);
        }

        return array;
    }

    public Array<int[]> getInRange(float x, float y, int range) {
        int indexX = 0;
        int indexY = 0;

        Array<int[]> list = new Array<>();

        // 减小范围，检查距离
        for (int[] i : map) {
            if (Math.abs(x - indexX) > range) {
                indexX++;
                continue;
            }

            indexY = 0;
            for (int j : i) {
                if (Math.abs(y - indexY) > range) {
                    indexY++;
                    continue;
                }
                // 检测距离
                if (getDistance(x, y, indexX, indexY) <= range) {
                    if (j != 0) {
                        list.add(new int[] { (int) indexX, (int) indexY });
                    }
                }
                indexY++;
            }
            indexX++;
        }

        return list;
    }

    public void setInRange(float x, float y, int min, int max) {
        isReseted = false;

        setRange((int) x, (int) y, max, 2);
        setRange((int) x, (int) y, min, 1);
    }

    public void setInLine(float x, float y, int direction, int distance, int status) {
        /*
         * ........2.....1
         * .........\.../
         * ..........\./
         * ....3------+------0
         * ........../.\
         * ........./...\
         * ........4.....5
         * 
         */

        switch (direction) {
            case 0:
                for (int i = distance; i > 0; i--) {
                    setPoint(x + i, y, status);
                }
                break;

            case 1:
                for (int i = distance; i > 0; i--) {
                    setPoint(x + i, y + i, status);
                }
                break;

            case 2:
                for (int i = distance; i > 0; i--) {
                    setPoint(x, y + i, status);
                }
                break;

            case 3:
                for (int i = distance; i > 0; i--) {
                    setPoint(x - i, y, status);
                }
                break;

            case 4:
                for (int i = distance; i > 0; i--) {
                    setPoint(x - i, y - i, status);
                }
                break;

            case 5:
                for (int i = distance; i > 0; i--) {
                    setPoint(x, y - i, status);
                }
                break;
        }
    }

    public void setInLines(float x, float y, int distance, int status) {
        for (int i = 5; i >= 0; i--) {
            setInLine(x, y, i, distance, status);
        }
    }

    public void draw(Batch batch) {
        int indexX = 0;
        int indexY = 0;

        batch.begin();
        for (int[] i : map) {
            indexY = 0;
            for (int j : i) {
                switch (j) {
                    case 0:
                        batch.setColor(0, 0, 0, 0);
                        break;
                    case 1:
                        batch.setColor(1, 1, 1, 1);
                        break;
                    case 2:
                        batch.setColor(0, 1, 0, 1);
                        break;
                    case 3:
                        batch.setColor(1, 0, 0, 1);
                        break;
                }

                drawMapPosition.set(getAbsPosition(indexX, indexY));
                batch.draw(groundImage, drawMapPosition.x - 25, drawMapPosition.y - 12, 50, 24);
                // 防止batch颜色错误！！！
                batch.setColor(1, 1, 1, 1);

                indexY++;
            }
            indexX++;
        }
        batch.end();
    }
}
