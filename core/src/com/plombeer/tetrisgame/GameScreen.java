package com.plombeer.tetrisgame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

/**
 * Created by hh on 12.06.2015.
 */
public class GameScreen implements Screen{


    public static int SCREEN_WIDTH = Gdx.graphics.getWidth();
    public static int SCREEN_HEIGHT = Gdx.graphics.getHeight();
    public static float METEOR_SIZE; //Размер каждого блока
    private float EPSILON; //Отступ, чтобы кубы не прилипали друг к другу

    private SpriteBatch m_batch;
    private ShapeRenderer m_shapeRenderer; //Данный ShapeRenderer отвечает за отрисовку, движущихся фигур
    private ShapeRenderer um_shapeRenderer;//Данный ShapeRenderer отвечает за отрисовку, не движущихся фигур

    /**
     * Поле игры.
     * Включает в себя матрицу, в которой записан тип каждой клеточки и ее цвет - > ArrayList<ArrayList<Element>> gamePole
     * Включает список стандартных цветов блоков -> ArrayList<Color> standardColors
     * Включает количество блоков, которое отображает экран по вертикали -> visibleH
     * (по горизонтали отображает количество блоков, которые убирается на экране)
     * Включет списов типов фигур, которые берутся из файла "figures.txt" ->  Vector<Type> types
     */
    private Pole pole;

    //Таймеры для анимации
    private MiniTimer moveTimer; //Таймер падения фигуры
    private MiniTimer rotateTimer; //Таймер поворта фигуры
    private MiniTimer deleteTimer; //Таймер исчезания блоков, которые выстроились в квадрат
    private MiniTimer gameOverTimer; //Таймер выпадания надписи GameOver и кнопки NewGame
    private MiniTimer putDownTimer; //Таймер выпадания надписи GameOver и кнопки NewGame

    private OrthographicCamera camera;

    private float angle = 0f; //Угол на который планета уже повернулась во время анимации
    private float angleVel; //Скорость поворота планеты

    /**
     * Так поле квадратное, у нас отображается не вся матрица на экране, поэтому
     * нужна координата (x, y), которая будет началом координат в нашей игры
     * -> Vector2 start;
     */
    private Vector2 startPos;

    private int planetSize;
    private int planetX;
    private int planetY;
    private Rectangle planet;

    //Кнопки управления
    public ImageButton leftButton;
    public ImageButton rightButton;
    public ImageButton rotateButton;
    public ImageButton downButton;
    public ImageButton newGameButton;
    public ImageButton pauseButton;

    public boolean qLose = false;
    private int score = 0;

    /**
     * Массив кубов, которые удаляются. Сделан для анимации удаления,
     * в которой мы постепенно уменьшаем deleteCubes.get(i).size
     */
    private ArrayList<DeleteCube> deleteCubes;
    private Sprite gameOverSprite;
    private Sprite pauseSprite;

    public Pole getPole() {
        return pole;
    }

    /**
     * Рисует прямоугольник с закругленными углами
     * @param shapeRenderer место для рисования
     * @param x координата x
     * @param y координата y
     * @param width ширина прямоугольника
     * @param height высота прямоугольника
     * @param r радиус закругения углов
     * @param color цвет прямоугольника
     */
    private void drawRoundRect(ShapeRenderer shapeRenderer, float x, float y, float width, float height, float r, Color color){
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(color.R / 255f, color.G / 255f, color.B / 255f, 1f);
        shapeRenderer.rect(x + r, y, width - 2 * r, height);
        shapeRenderer.rect(x, y + r, width, height - 2 * r);

        shapeRenderer.arc(x + width - r, y + height - r, r, 0, 90);
        shapeRenderer.arc(x + r, y + height - r, r, 90, 90);
        shapeRenderer.arc(x + r, y + r, r, 180, 90);
        shapeRenderer.arc(x + width - r, y + r, r, 270, 90);

        shapeRenderer.end();
    }

    static class DeleteCube{
        int x;
        int y;
        Color color;
        float size;

        DeleteCube(int x, int y, Color color, float size) {
            this.x = x;
            this.y = y;
            this.color = color;
            this.size = size;
        }
    }


    /**
     * Функция проигрыша выключает все таймеры и включает gameOverTimer
     */
    public void loseFunction(){
        moveTimer.disabled();
        deleteTimer.disabled();
        rotateTimer.disabled();
        gameOverTimer.enabled();
    }

    /**
     * Начало новой игры ->
     * 1. Отчищение поля игры
     * 2. Ставим надпись GameOver и кнопку NewGame на свои начальные места
     * 3. Создаем сразу новую фигуру
     * 4. Включаем таймер движения
     */
    public void newGame(){
        score = 0;
        qLose = false;
        for (int i = 0; i < pole.N; i++) {
            for (int j = 0; j < pole.N; j++) {
                if(!(i >= planetX && i < planetX + planetSize && j >= planetY && j < planetY + planetSize)){
                    pole.setTypeOfElement(Pole.TypeOfElement.empty, i, j);
                    pole.getElement(i, j).color.set(255, 255, 255);
                }
            }
        }

        newGameButton.setPos(SCREEN_WIDTH / 2f - SCREEN_WIDTH / 8f, SCREEN_HEIGHT + 3f);
        gameOverSprite.setPosition(SCREEN_WIDTH / 2f - gameOverSprite.getWidth() / 2f, SCREEN_HEIGHT + newGameButton.getHeight() + 0.5f * gameOverSprite.getHeight());

        pole.makeNewFigure();

        moveTimer.enabled();
    }

    /**
     * Инициализация всех таймеров ->
     * 1. Включен изначально таймер или нет
     * 2. Интервал работы таймера
     * 3. Что делать таймеру, каждую отрисовку экрана
     * 4. ЧТо делать таймеру после окончания работы
     */
    private void timerInit(){
        moveTimer = new MiniTimer(false, 0.7f);
        moveTimer.setTasker(new Tasker() {
            @Override
            public void doAfterTime() {
                if(!pole.move(Pole.TypeOfMove.down)) {
                   doAfterFall();
                }
                if(!qLose)
                    moveTimer.enabled();
            }

            @Override
            public void onTick(float delta) {

            }
        });

        rotateTimer = new MiniTimer(true, 0.3f);
        angleVel = 90f / rotateTimer.getInterval();
        rotateTimer.setTasker(new Tasker() {
            @Override
            public void doAfterTime() {
                camera.rotate(-angle);
                camera.update();
                angle = 0f;
                rebuildPole();
                if(qLose)
                    loseFunction(); else
                    moveTimer.enabled();
            }

            @Override
            public void onTick(float delta) {
                float d = angleVel * delta;
                angle += d;
                camera.rotate(d);
                camera.update();
            }
        });

        deleteTimer = new MiniTimer(false, 0.45f);
        final float deleteCubesVel = METEOR_SIZE / 2 / deleteTimer.getInterval();
        deleteTimer.setTasker(new Tasker() {
            @Override
            public void doAfterTime() {
                deleteCubes.clear();
            }

            @Override
            public void onTick(float delta) {
                for (int i = 0; i < deleteCubes.size(); i++) {
                    deleteCubes.get(i).size -= deleteCubesVel * delta;
                }
            }
        });

        gameOverTimer = new MiniTimer(false, 1f);
        final float gameOverSpriteVel = -SCREEN_HEIGHT / 2f / gameOverTimer.getInterval();
        gameOverTimer.setTasker(new Tasker() {
            @Override
            public void doAfterTime() {

            }

            @Override
            public void onTick(float delta) {
                gameOverSprite.translateY(gameOverSpriteVel * delta);
                newGameButton.translateY(gameOverSpriteVel * delta);
            }
        });

        putDownTimer = new MiniTimer(false, 0.01f);
        putDownTimer.setTasker(new Tasker() {
            @Override
            public void doAfterTime() {
                if(pole.move(Pole.TypeOfMove.down))
                    putDownTimer.enabled(); else
                    doAfterFall();
            }

            @Override
            public void onTick(float delta) {

            }
        });
    }

    /**
     * Инициализация кнопок ->
     * 1. Координаты
     * 2. Размеры
     * 3. Текстура для отображения
     * 4. Отображения и поврот на 90
     * 5. Прозрачность текстуры
     */
    private void buttonsInit(){
        leftButton = new ImageButton(7f * SCREEN_WIDTH / 60f, SCREEN_HEIGHT / 12f, SCREEN_WIDTH / 6f, SCREEN_WIDTH / 6f,"textures/arrow.png", false, false, 0.5f);
        leftButton.setClikedInterface(new ClikedInterface() {
            @Override
            public void onClick() {
                pole.move(Pole.TypeOfMove.left);
            }
        });
        rightButton = new ImageButton(43f * SCREEN_WIDTH / 60f, SCREEN_HEIGHT / 12f, SCREEN_WIDTH / 6f,SCREEN_WIDTH / 6f,"textures/arrow.png", true, false, 0.5f);
        rightButton.setClikedInterface(new ClikedInterface() {
            @Override
            public void onClick() {
                pole.move(Pole.TypeOfMove.right);
            }
        });

        rotateButton = new ImageButton(SCREEN_WIDTH / 2f - SCREEN_WIDTH / 14f, SCREEN_HEIGHT / 12f + SCREEN_WIDTH / 8f, SCREEN_WIDTH / 7f, SCREEN_WIDTH / 7f,"textures/arrow_rotate.png", false, false, 0.5f);
        rotateButton.setClikedInterface(new ClikedInterface() {
            @Override
            public void onClick() {
                rotatePlanet();
            }
        });

        downButton = new ImageButton(SCREEN_WIDTH / 2f - SCREEN_WIDTH / 14f, SCREEN_HEIGHT / 12f - SCREEN_WIDTH / 8f, SCREEN_WIDTH / 7f, SCREEN_WIDTH / 7f,"textures/arrow.png", false, true, 0.5f);
        downButton.setClikedInterface(new ClikedInterface() {
            @Override
            public void onClick() {
                putDown();
            }
        });

        newGameButton = new ImageButton(SCREEN_WIDTH / 2f - SCREEN_WIDTH / 8f, SCREEN_HEIGHT + 3f, SCREEN_WIDTH / 4f, SCREEN_WIDTH / 16f, "textures/new_game.png", false, false, 1f);
        newGameButton.setClikedInterface(new ClikedInterface() {
            @Override
            public void onClick() {
                newGame();
            }
        });

        pauseButton = new ImageButton(SCREEN_WIDTH * 0.921f, SCREEN_HEIGHT - SCREEN_WIDTH * 0.0806f, SCREEN_WIDTH * 0.0806f, SCREEN_WIDTH * 0.0806f, "textures/pause.png", false, false, 0.5f);
        pauseButton.setClikedInterface(new ClikedInterface() {
            @Override
            public void onClick() {
                pauseGame(true);
            }
        });
    }

    private boolean lastEnabled[] = new boolean[4];
    public boolean isPaused = false;
    public void pauseGame(boolean pause){
        isPaused = pause;
        if(pause) {
            lastEnabled[0] = moveTimer.isEnabled();
            lastEnabled[1] = rotateTimer.isEnabled();
            lastEnabled[2] = deleteTimer.isEnabled();
            lastEnabled[3] = putDownTimer.isEnabled();
            moveTimer.disabled();
            rotateTimer.disabled();
            deleteTimer.disabled();
            putDownTimer.disabled();
        } else{
            if(lastEnabled[0]) moveTimer.enabled();
            if(lastEnabled[1]) rotateTimer.enabled();
            if(lastEnabled[2]) deleteTimer.enabled();
            if(lastEnabled[3]) putDownTimer.enabled();
        }
    }

    @Override
    public void show() {
        m_batch = new SpriteBatch();
        m_shapeRenderer = new ShapeRenderer();
        um_shapeRenderer = new ShapeRenderer();
        deleteCubes = new ArrayList<DeleteCube>();

        pole = new Pole(38, 30);

        METEOR_SIZE = SCREEN_HEIGHT / (float)pole.getVisibleH();
        EPSILON = METEOR_SIZE / 20f;
        startPos = new Vector2((pole.N * METEOR_SIZE - SCREEN_WIDTH) / 2,  (pole.N - pole.getVisibleH()) / 2 * METEOR_SIZE);

        camera = new OrthographicCamera(SCREEN_WIDTH, SCREEN_HEIGHT);
        camera.translate(SCREEN_WIDTH / 2, SCREEN_HEIGHT / 2);
        camera.update();

        pole.makeNewFigure();

        planetSize = 4;
        planetX = pole.N / 2 - planetSize / 2;
        planetY = pole.N / 2 - planetSize / 2;

        /**
         * Клеточки на которых стоит планета мы инициилизируем как Pole.TypeOfElement.um_meteor,
         * чтобы на них падали блоки, а не пролетали мимо
         */
        for (int i = 0; i < planetSize; i++) {
            for (int j = 0; j < planetSize; j++) {
                pole.setTypeOfElement(Pole.TypeOfElement.um_meteor, pole.N / 2 - planetSize / 2 + i, pole.N / 2 - planetSize / 2 + j);
            }
        }

        //Иницилиазия координаты и размеры планеты в центре
        planet = new Rectangle(planetX * METEOR_SIZE - startPos.x, planetY * METEOR_SIZE  - startPos.y, METEOR_SIZE * planetSize, METEOR_SIZE * planetSize);

        //Строит вокруг планеты квадрат из блоков. Сделан только для тестирования
        for (int i = -1; i < planetSize; i++) {
            for (int j = 0; j < 2; j++) {
                pole.setTypeOfElement(Pole.TypeOfElement.um_meteor, pole.N / 2 - planetSize / 2 - 1 - j, pole.N / 2 - planetSize / 2 + i);
                pole.getElement(pole.N / 2 - planetSize / 2 - 1 - j, pole.N / 2 - planetSize / 2 + i).color.set(pole.getColorByIndex(1));

                pole.setTypeOfElement(Pole.TypeOfElement.um_meteor, pole.N / 2 - planetSize / 2 + i, pole.N / 2 - planetSize / 2 - 1 - j);
                pole.getElement(pole.N / 2 - planetSize / 2 + i, pole.N / 2 - planetSize / 2 - 1 - j).color.set(pole.getColorByIndex(1));

                pole.setTypeOfElement(Pole.TypeOfElement.um_meteor, pole.N / 2 + planetSize / 2 + j, pole.N / 2 - planetSize / 2 + i);
                pole.getElement(pole.N / 2 + planetSize / 2 + j, pole.N / 2 - planetSize / 2 + i).color.set(pole.getColorByIndex(1));

                pole.setTypeOfElement(Pole.TypeOfElement.um_meteor, pole.N / 2 - planetSize / 2 + i, pole.N / 2 + planetSize / 2 + j);
                pole.getElement(pole.N / 2 - planetSize / 2 + i, pole.N / 2 + planetSize / 2 + j).color.set(pole.getColorByIndex(1));
            }
        }


        timerInit();
        buttonsInit();

        gameOverSprite = new Sprite(new Texture("textures/Game_over.png"));
        gameOverSprite.setSize(SCREEN_WIDTH / 2f, SCREEN_WIDTH / 8f);
        gameOverSprite.setPosition(SCREEN_WIDTH / 2f - gameOverSprite.getWidth() / 2f, SCREEN_HEIGHT + newGameButton.getHeight() + 0.5f * gameOverSprite.getHeight());

        pauseSprite = new Sprite(new Texture("textures/pause_label.png"));
        pauseSprite.setSize(SCREEN_WIDTH / 4f, SCREEN_WIDTH / 10f);
        pauseSprite.setPosition(SCREEN_WIDTH / 2f - pauseSprite.getWidth() / 2, SCREEN_HEIGHT / 2f - pauseSprite.getHeight() / 2);
        Gdx.input.setInputProcessor(new InputListener(this));
    }

    // Что мы делаем после того, как фигура упала на неподвижный блок
    private void doAfterFall(){
        if (!pole.makeNewFigure()){
            qLose = true;
            loseFunction();
        } else {
            deleteCubes.clear();
            if (pole.findSquare(planetSize, deleteCubes)) {
                if (deleteCubes.size() != 0)
                    deleteTimer.enabled();
            }

            for (int i = 0; i < pole.N; i++) {
                for (int j = 0; j < (pole.N - pole.getVisibleH()) / 2; j++) {
                    pole.setTypeOfElement(Pole.TypeOfElement.empty, i, j);
                    pole.getElement(i, j).color.set(255, 255, 255);
                }
            }
        }
    }

    //Опустить фигуру в самый низ
    public void putDown(){
        putDownTimer.enabled();
    }

    //Пореворачиваем массив
    private void rebuildPole(){

        ArrayList<Vector2> moved_blocks = new ArrayList<Vector2>();
        ArrayList<Color> moved_blocks_color = new ArrayList<Color>();

        for (int i = 0; i < pole.N; i++) {
            for (int j = 0; j < pole.N; j++) {
                if(pole.getElement(i, j).type.equals(Pole.TypeOfElement.m_meteor)){
                    moved_blocks.add(new Vector2(i, j));
                    moved_blocks_color.add(pole.getElement(i, j).color);
                    pole.setTypeOfElement(Pole.TypeOfElement.empty, i, j);
                }
            }
        }

        Pole.Element[][] a = new Pole.Element[pole.N][pole.N];
        for (int i = 0; i < pole.N; i++) {
            for (int j = 0; j < pole.N; j++) {
                a[i][j] = new Pole.Element(pole.getElement(i, j));
            }
        }

        int p = pole.N / 2;
        int n = pole.N;
        for(int r = 1; r <= p; r++) {
            for(int c = r; c <= n - r; c++) {
                Pole.Element x = new Pole.Element(a[r - 1][c - 1].type, a[r - 1][c - 1].color.R, a[r - 1][c - 1].color.G, a[r - 1][c - 1].color.B);
                a[r - 1][c - 1].set(a[c - 1][n - r]);
                a[c - 1][n - r].set(a[n - r][n - c]);
                a[n - r][n - c].set(a[n - c][r - 1]);
                a[n - c][r - 1].set(x);
            }
        }

        for (int i = 0; i < pole.N; i++) {
            for (int j = 0; j < pole.N; j++) {
                pole.setElement(a[i][j], i, j);
            }
        }

        for (int i = 0; i < moved_blocks.size(); i++) {
            if(pole.getElement((int) moved_blocks.get(i).x, (int) moved_blocks.get(i).y).type.equals(Pole.TypeOfElement.um_meteor)){
                qLose = true;
            }
            pole.setTypeOfElement(Pole.TypeOfElement.m_meteor, (int) moved_blocks.get(i).x, (int) moved_blocks.get(i).y);
            pole.getElement((int) moved_blocks.get(i).x, (int) moved_blocks.get(i).y).color.set(moved_blocks_color.get(i));
        }
    }

    //Поворот планеты
    public void rotatePlanet(){
        moveTimer.disabled();
        rotateTimer.enabled();
    }


    /**
     *
     * @param shapeRenderer
     * ShapeRenderer на котором будут отображатся фигуры типа t
     * @param t
     * Тип фигур, которые мы будем рисовать
     */
    private void drawElements(ShapeRenderer shapeRenderer, Pole.TypeOfElement t){

        for (int i = 0; i < pole.N; i++) {
            for (int j = 0; j < pole.N; j++) {
                if(!pole.getElement(i, j).type.equals(Pole.TypeOfElement.empty)){
                    if(pole.getElement(i, j).type.equals(t)){
                        float x = -startPos.x + i * METEOR_SIZE + EPSILON;
                        float y = -startPos.y + j * METEOR_SIZE + EPSILON;
                        float size = METEOR_SIZE - 2 * EPSILON;
                        drawRoundRect(shapeRenderer, x, y, size, size, METEOR_SIZE / 10f, pole.getElement(i, j).color);
                    }
                }
            }
        }

    }

    //Тик всех таймеров
    private void tickTimers(float delta){
        moveTimer.tick(delta);
        rotateTimer.tick(delta);
        deleteTimer.tick(delta);
        gameOverTimer.tick(delta);
        putDownTimer.tick(delta);
    }

    private void drawButtons(SpriteBatch batch){
        leftButton.draw(batch);
        rightButton.draw(batch);
        rotateButton.draw(batch);
        downButton.draw(batch);
        pauseButton.draw(batch);
    }
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(248 / 255f, 248 / 255f, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        /**
         * Ставим камеру на um_shapeRenderer, потому что при повороте камеры должны
         * поворачиваться лишь фигуры, которые уже стоят на планете
         */
        um_shapeRenderer.setProjectionMatrix(camera.combined);

        //Рисуем неподвижные блоки
        drawElements(um_shapeRenderer, Pole.TypeOfElement.um_meteor);

        //Рисуем движущиеся блоки
        drawElements(m_shapeRenderer, Pole.TypeOfElement.m_meteor);

        //Рисуем планету
        drawRoundRect(um_shapeRenderer, planet.getX(), planet.getY(), planet.getWidth(), planet.getHeight(), planet.getWidth() * 0.1f, pole.getColorByIndex(0));

        float EPSILON = METEOR_SIZE / 20f;

        for (int i = 0; i < deleteCubes.size(); i++) {
            drawRoundRect(um_shapeRenderer, -startPos.x + deleteCubes.get(i).x * METEOR_SIZE + EPSILON + (METEOR_SIZE - deleteCubes.get(i).size) / 2, -startPos.y + deleteCubes.get(i).y * METEOR_SIZE + EPSILON+ (METEOR_SIZE - deleteCubes.get(i).size) / 2, deleteCubes.get(i).size - 2 * EPSILON, deleteCubes.get(i).size - 2 * EPSILON, deleteCubes.get(i).size / 10, deleteCubes.get(i).color);
        }

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        drawButtons(m_batch);

        if(qLose){
            m_batch.begin();
            gameOverSprite.draw(m_batch);
            m_batch.end();
            newGameButton.draw(m_batch);
        }

        if(isPaused){
            m_batch.begin();
            pauseSprite.draw(m_batch);
            m_batch.end();
        }

        Gdx.gl.glDisable(GL20.GL_BLEND);

        tickTimers(delta);

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
        m_shapeRenderer.dispose();
        um_shapeRenderer.dispose();
    }
}
