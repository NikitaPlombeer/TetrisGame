package com.plombeer.tetrisgame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.Random;
import java.util.Vector;

/**
 * Created by hh on 12.06.2015.
 */
public class Pole {

    public int N;
    private ArrayList<Color> standardColors;

    enum TypeOfElement{
        m_meteor, um_meteor, empty
    }

    //Количество блоков, которое отображает экран по вертикали
    private int visibleH;

    //Матрица поля состоит из
    static class Element{
        Pole.TypeOfElement type;
        Color color;

        Element() {
            color = new Color();
            type = TypeOfElement.empty;
        }

        Element(Element element) {
            color = new Color();
            set(element);
        }

        Element(TypeOfElement t, int R, int G, int B){
            color = new Color(R, G, B);
            type = t;
        }

        void set(Element element){
            type = element.type;
            color.R = element.color.R;
            color.G = element.color.G;
            color.B = element.color.B;
        }
    }


    private ArrayList<ArrayList<Element>> gamePole;
    private Vector<Type> types; // Типы фигур


    Pole(int N, int visibleH){
        this.N = N;
        this.visibleH = visibleH;

        loadTypes();

        gamePole = new ArrayList<ArrayList<Element>>();
        for (int i = 0; i < N; i++) {
            gamePole.add(new ArrayList<Element>());
            for (int j = 0; j < N; j++) {
                gamePole.get(i).add(new Element());
            }
        }
        standardColors = new ArrayList<Color>();
        standardColors.add(new Color(243, 66, 53));
        standardColors.add(new Color(232, 29, 98));
        standardColors.add(new Color(155, 38, 175));
        standardColors.add(new Color(102, 57, 182));
        standardColors.add(new Color(63, 81, 181));
        standardColors.add(new Color(33, 150, 243));
        standardColors.add(new Color(2, 168, 243));
        standardColors.add(new Color(0, 188, 212));
        standardColors.add(new Color(0, 150, 136));
        standardColors.add(new Color(254, 192, 6));
        standardColors.add(new Color(254, 234, 58));
        standardColors.add(new Color(204, 219, 56));
        standardColors.add(new Color(138, 194, 73));
        standardColors.add(new Color(75, 174, 79));
        standardColors.add(new Color(254, 151, 0));
        standardColors.add(new Color(254, 86, 33));
        standardColors.add(new Color(120, 84, 71));
        standardColors.add(new Color(157, 157, 157));
        standardColors.add(new Color(96, 125, 139));
    }

    public int getVisibleH() {
        return visibleH;
    }

    public Element getElement(int x, int y){
        return gamePole.get(x).get(y);
    }

    public void setElement(Element t, int x, int y){
        gamePole.get(x).set(y, t);
    }

    public void setTypeOfElement(TypeOfElement type, int x, int y){
        gamePole.get(x).get(y).type = type;
    }

    public void gravity(){
        
    }

    public Color getColorByIndex(int index){
        if(index >= standardColors.size()) return new Color(0, 0, 0);
        return standardColors.get(index);
    }

    /**
     * Функция создает новую фигуру
     * @return True, если при повявлении новой фигуры, она не попадает на блок
     */
    public boolean makeNewFigure(){
        Random rand = new Random();
        int index = rand.nextInt(types.size());

        int index_c = rand.nextInt(standardColors.size());

        boolean check = true;
        for (int i = 0; i < types.get(index).W; i++) {
            for (int j = 0; j < types.get(index).H; j++) {
                if(types.get(index).figs[i][j]) {
                    if(getElement(N / 2 - types.get(index).W / 2 + i, N - j - 1).type.equals(TypeOfElement.um_meteor))
                        check = false;
                    setTypeOfElement(TypeOfElement.m_meteor, N / 2 - types.get(index).W / 2 + i, N - j - 1);
                    gamePole.get(N / 2 - types.get(index).W / 2 + i).get(N - j - 1).color.set(getColorByIndex(index_c));
                }
            }
        }

        return check;
    }

    //Ищем и удаляем квадрат на поле
    public boolean findSquare(int planetSize, ArrayList<GameScreen.DeleteCube> deleteCubes){
        int planetX = N / 2 - planetSize / 2;
        int planetY = N / 2 - planetSize / 2;

        planetX--;
        planetY--;

        boolean qCheck = false;


        for (int size = planetSize + 2; size < visibleH; size += 2) {

            for (int j = planetX; j < planetX + size; j++) {
                if(!getElement(j, planetY).type.equals(TypeOfElement.um_meteor) ||
                        !getElement(planetX, j).type.equals(TypeOfElement.um_meteor) ||
                        !getElement(j, planetY + size - 1).type.equals(TypeOfElement.um_meteor) ||
                        !getElement(planetX + size - 1, j).type.equals(TypeOfElement.um_meteor)) {
                    qCheck = true;
                    break;
                }
            }

            if(!qCheck){
                for (int j = planetX; j < planetX + size; j++) {
                    deleteCubes.add(new GameScreen.DeleteCube(j, planetY, getElement(j, planetY).color, GameScreen.METEOR_SIZE));
                    deleteCubes.add(new GameScreen.DeleteCube(planetX, j, getElement(planetX, j).color, GameScreen.METEOR_SIZE));
                    deleteCubes.add(new GameScreen.DeleteCube(j, planetY + size - 1, getElement(j, planetY + size - 1).color, GameScreen.METEOR_SIZE));
                    deleteCubes.add(new GameScreen.DeleteCube(planetX + size - 1, j, getElement(planetX + size - 1, j).color, GameScreen.METEOR_SIZE));

                    setTypeOfElement(TypeOfElement.empty, j, planetY);
                    setTypeOfElement(TypeOfElement.empty, planetX, j);
                    setTypeOfElement(TypeOfElement.empty, j, planetY + size - 1);
                    setTypeOfElement(TypeOfElement.empty, planetX + size - 1, j);
                }
            }
            planetX--;
            planetY--;
            qCheck = false;
        }
        return !qCheck;
    }

    //Загружаем фигуры из файла в Vector<Type> types
    private void loadTypes(){
        types = new Vector<Type>();
        FileHandle file = Gdx.files.internal("figures.txt");
        String s = file.readString();
        String str[] = s.split("\r\n");
        for (int i = 0; i < str.length; i++) {
            String size[] = str[i].split(" ");
            int W = Integer.parseInt(size[0]);
            int H = Integer.parseInt(size[1]);
            boolean fig[][] = new boolean[W][H];

            for (int j = 0; j < H; j++) {
                String stroke[] = str[i + j + 1].split(" ");
                for (int k = 0; k < W; k++) {
                    fig[k][j] = stroke[k].equals("1") ? true : false;
                }
            }
            types.add(new Type(W, H, fig));
            i += H;
        }
    }

    enum TypeOfMove{
        down, right, left
    }

    //Двигаем фигуру внизи или впрво или влево, в зависимости от t
    public boolean move(TypeOfMove t){
        Vector2 m = new Vector2(0, 0);
        switch (t){
            case down:
                m.set(0, -1);
                break;
            case left:
                m.set(-1, 0);
                break;
            case right:
                m.set(1, 0);
                break;
        }

        ArrayList<Vector2> moved_blocks = new ArrayList<Vector2>();
        ArrayList<Color> moved_blocks_color = new ArrayList<Color>();
        boolean qCheck = true;
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if(getElement(i, j).type.equals(TypeOfElement.m_meteor)){
                    moved_blocks.add(new Vector2(i, j));
                    moved_blocks_color.add(new Color(getElement(i, j).color));
                    if(i + (int)m.x < N && j + (int)m.y < N && i + (int)m.x >= 0 && j + (int)m.y >= 0 && (getElement(i + (int)m.x, j + (int)m.y).type.equals(TypeOfElement.um_meteor) || j + (int)m.y == 0)){
                        qCheck = false;
                        break;
                    }
                }
            }
            if(!qCheck) break;
        }

        if(qCheck) {
            for (int i = 0; i < moved_blocks.size(); i++) {
                setTypeOfElement(TypeOfElement.empty, (int) moved_blocks.get(i).x, (int) moved_blocks.get(i).y);
                getElement((int) moved_blocks.get(i).x, (int) moved_blocks.get(i).y).color.set(255, 255, 255);

            }
            for (int i = 0; i < moved_blocks.size(); i++) {
                setTypeOfElement(TypeOfElement.m_meteor, (int) (moved_blocks.get(i).x + m.x), (int) (moved_blocks.get(i).y + m.y));
                getElement((int) (moved_blocks.get(i).x + m.x), (int) (moved_blocks.get(i).y + m.y)).color.set(moved_blocks_color.get(i));

            }
        }else{
            if(t.equals(TypeOfMove.down)) {
                for (int i = 0; i < N; i++)
                    for (int j = 0; j < N; j++)
                        if (getElement(i, j).type.equals(TypeOfElement.m_meteor)) {
                            setTypeOfElement(TypeOfElement.um_meteor, i, j);
                        }

            }
            return false;
        }
        return true;
    }
}
