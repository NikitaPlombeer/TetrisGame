package com.plombeer.tetrisgame;

import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

/**
 * Created by hh on 12.06.2015.
 */
public class Pole {


    public int N;
    enum TypeOfElement{
        m_meteor, um_meteor, empty
    }
    private ArrayList<ArrayList<TypeOfElement>> gamePole;
    private ArrayList<Vector2> moved_blocks;

    public TypeOfElement getElement(int x, int y){
        return gamePole.get(x).get(y);
    }

    public void setElement(TypeOfElement t, int x, int y){
        gamePole.get(x).set(y, t);
    }

    Pole(int N){
        this.N = N;
        gamePole = new ArrayList<ArrayList<TypeOfElement>>();
        for (int i = 0; i < N; i++) {
            gamePole.add(new ArrayList<TypeOfElement>());
            for (int j = 0; j < N; j++) {
                gamePole.get(i).add(TypeOfElement.empty);
            }
        }

    }

    enum TypeOfMove{
        down, right, left
    }
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
        boolean qCheck = true;
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if(getElement(i, j).equals(TypeOfElement.m_meteor)){
                    moved_blocks.add(new Vector2(i, j));
                    if(i + (int)m.x < N && j + (int)m.y < N && i + (int)m.x >= 0 && j + (int)m.y >= 0 && getElement(i + (int)m.x, j + (int)m.y).equals(TypeOfElement.um_meteor)){
                        qCheck = false;
                        break;
                    }
                }
            }
            if(!qCheck) break;
        }

        if(qCheck) {
            System.out.println("{ ");
            for (int i = 0; i < moved_blocks.size(); i++) {
                setElement(TypeOfElement.empty, (int) moved_blocks.get(i).x, (int) moved_blocks.get(i).y);
                System.out.print("("+(int) moved_blocks.get(i).x+" , "+ (int) moved_blocks.get(i).y+") ");
            }
            System.out.print(" }");
            for (int i = 0; i < moved_blocks.size(); i++) {
                setElement(TypeOfElement.m_meteor, (int) (moved_blocks.get(i).x + m.x), (int) (moved_blocks.get(i).y + m.y));
            }
        }else{
            if(t.equals(TypeOfMove.down)) {
                for (int i = 0; i < N; i++)
                    for (int j = 0; j < N; j++)
                        if (getElement(i, j).equals(TypeOfElement.m_meteor))
                            setElement(TypeOfElement.um_meteor, i, j);

            }
            return false;
        }
        return true;
    }
}
