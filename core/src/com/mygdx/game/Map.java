package com.mygdx.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
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
        // 以range范围设置地图块
        int indexX = 0;
        int indexY = 0;

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
                if (Consts.getDistance(x, y, indexX, indexY) <= range) {
                    if (j != 0) {
                        setPoint(indexX, indexY, status);
                    }
                }
                indexY++;
            }
            indexX++;
        }
    }

    public void setInRange(int x, int y, int min, int max) {
        isReseted = false;

        setRange(x, y, max, 2);
        setRange(x, y, min, 1);
    }

    public void setInRange(float x, float y, int min, int max) {
        setInRange((int) x, (int) y, min, max);
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

    @Override
    public void draw(Batch batch, float parentAlpha) {

        int indexX = 0;
        int indexY = 0;

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

                drawMapPosition.set(Consts.getAbsPosition(indexX, indexY));
                batch.draw(groundImage, drawMapPosition.x - 25, drawMapPosition.y - 12, 50, 24);
                // 防止batch颜色错误！！！
                batch.setColor(1, 1, 1, 1);

                indexY++;
            }
            indexX++;
        }
    }
}
