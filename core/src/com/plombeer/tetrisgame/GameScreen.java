package com.plombeer.tetrisgame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

/**
 * Created by hh on 12.06.2015.
 */
public class GameScreen implements Screen{


    public static int SCREEN_WIDTH = Gdx.graphics.getWidth();
    public static int SCREEN_HEIGHT = Gdx.graphics.getHeight();
    public static float METEOR_SIZE;

    private int visibleH;

    private SpriteBatch m_batch;
    private SpriteBatch um_batch;
    private Sprite meteor;
    private Sprite background;


    private Pole pole;

    private MiniTimer moveTimer;
    private MiniTimer rotateTimer;

    private OrthographicCamera camera;

    float angle = 0f;
    float angleVel;

    @Override
    public void show() {
        m_batch = new SpriteBatch();
        um_batch = new SpriteBatch();

        meteor = new Sprite(new Texture("meteor.png"));

        background = new Sprite(new Texture("background.png"));
        background.setPosition(0, 0);
        background.setSize(SCREEN_WIDTH, SCREEN_HEIGHT);

        pole = new Pole(26);
        visibleH = 20;

        METEOR_SIZE = SCREEN_HEIGHT / (float)visibleH;

        camera = new OrthographicCamera(SCREEN_WIDTH, SCREEN_HEIGHT);
        camera.translate(SCREEN_WIDTH / 2, SCREEN_HEIGHT / 2);
        camera.update();

        pole.setElement(Pole.TypeOfElement.m_meteor, 8, 25);
        pole.setElement(Pole.TypeOfElement.m_meteor, 9, 25);
        pole.setElement(Pole.TypeOfElement.m_meteor, 10, 25);
        pole.setElement(Pole.TypeOfElement.m_meteor, 11, 25);

        pole.setElement(Pole.TypeOfElement.um_meteor, pole.N / 2, pole.N / 2);
        pole.setElement(Pole.TypeOfElement.um_meteor, pole.N / 2 - 1, pole.N / 2);
        pole.setElement(Pole.TypeOfElement.um_meteor, pole.N / 2, pole.N / 2 - 1);
        pole.setElement(Pole.TypeOfElement.um_meteor, pole.N / 2 - 1, pole.N / 2 - 1);
        meteor.setSize(METEOR_SIZE, METEOR_SIZE);

        for (int i = 0; i < pole.N; i++) {
            pole.setElement(Pole.TypeOfElement.um_meteor, i, pole.N / 2);
            pole.setElement(Pole.TypeOfElement.um_meteor, pole.N / 2, i);
        }


        moveTimer = new MiniTimer(true, 0f, 1f);
        moveTimer.setTasker(new Tasker() {
            @Override
            public void doAfterTime() {
                pole.move(Pole.TypeOfMove.down);
            }

            @Override
            public void onTick(float delta) {

            }
        });

        rotateTimer = new MiniTimer(true, 0f, 2f);
        angleVel = 90f / rotateTimer.getInterval();

        rotateTimer.setTasker(new Tasker() {
            @Override
            public void doAfterTime() {
                camera.rotate(-angle);
                camera.update();
                angle = 0f;
                rotateTimer.disabled();
                rebuildPole();
                meteor.rotate90(false);
            }

            @Override
            public void onTick(float delta) {
                float d = angleVel * delta;
                angle += d;
                camera.rotate(d);
                camera.update();
            }
        });


        Gdx.input.setInputProcessor(new InputListener(pole));
    }


    private void rebuildPole(){

        ArrayList<Vector2> moved_blocks = new ArrayList<Vector2>();

        for (int i = 0; i < pole.N; i++) {
            for (int j = 0; j < pole.N; j++) {
                if(pole.getElement(i, j).equals(Pole.TypeOfElement.m_meteor)){
                    moved_blocks.add(new Vector2(i, j));
                    pole.setElement(Pole.TypeOfElement.empty, i, j);
                }
            }
        }

        Pole.TypeOfElement[][] a = new Pole.TypeOfElement[pole.N][pole.N];
        for (int i = 0; i < pole.N; i++) {
            for (int j = 0; j < pole.N; j++) {
                a[i][j] = pole.getElement(i, j);
            }
        }

        int p = pole.N / 2;
        int n = pole.N;
        for(int r = 1; r <= p; r++) {
            for(int c = r; c <= n - r; c++) {
                Pole.TypeOfElement x = a[r - 1][c - 1];
                a[r - 1][c - 1] = a[c - 1][n - r];
                a[c - 1][n - r] = a[n - r][n - c];
                a[n - r][n - c] = a[n - c][r - 1];
                a[n - c][r - 1] = x;
            }
        }

        for (int i = 0; i < pole.N; i++) {
            for (int j = 0; j < pole.N; j++) {
                pole.setElement(a[i][j], i, j);
            }
        }

        for (int i = 0; i < moved_blocks.size(); i++) {
            pole.setElement(Pole.TypeOfElement.m_meteor, (int)moved_blocks.get(i).x, (int)moved_blocks.get(i).y);
        }
    }

    private void drawMeteor(SpriteBatch batch, Pole.TypeOfElement t){
        batch.begin();
        float startX = (pole.N * METEOR_SIZE - SCREEN_WIDTH) / 2;
        float startY = (pole.N - visibleH) / 2 * METEOR_SIZE;

        for (int i = 0; i < pole.N; i++) {
            for (int j = 0; j < pole.N; j++) {
                if(!pole.getElement(i, j).equals(Pole.TypeOfElement.empty)){
                    if(pole.getElement(i, j).equals(t)){
                        meteor.setPosition( -startX + i * METEOR_SIZE, -startY + j * METEOR_SIZE);
                        meteor.draw(batch);
                    }
                }
            }
        }

        batch.end();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        um_batch.setProjectionMatrix(camera.combined);

        m_batch.begin();
        background.draw(m_batch);
        m_batch.end();

        drawMeteor(um_batch, Pole.TypeOfElement.um_meteor);

        drawMeteor(m_batch, Pole.TypeOfElement.m_meteor);


        moveTimer.tick(delta);
        rotateTimer.tick(delta);
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        m_batch.dispose();
        um_batch.dispose();
    }
}
