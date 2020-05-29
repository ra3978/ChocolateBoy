package com.timewrap.chocolateboygame;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;
import java.util.Random;

public class ChocolateBoy extends ApplicationAdapter {
	SpriteBatch batch;
	Texture background;
	float x=0,y=0;
	//float speed;
	Texture[] man;
	int manState = 0;
	int pause = 0;
	float gravity = 0.2f;
	float velocity = 0;
	int manY = 0;
	Rectangle manRectangle;
	BitmapFont font;
	BitmapFont font2;
	Texture dizzy;

	int score = 0;

	int gameState = 0;
	Random random;

	ArrayList<Integer> coinXs = new ArrayList<Integer>();
	ArrayList<Integer> coinYs = new ArrayList<Integer>();
	ArrayList<Rectangle> coinRectangles = new ArrayList<Rectangle>();
	Texture coin;
	int coinCount;

	ArrayList<Integer> chocoXs = new ArrayList<Integer>();
	ArrayList<Integer> chocoYs = new ArrayList<Integer>();
	ArrayList<Rectangle> chocoRectangles = new ArrayList<Rectangle>();
	Texture choco;
	int chocoCount;

	ArrayList<Integer> bombXs = new ArrayList<Integer>();
	ArrayList<Integer> bombYs = new ArrayList<Integer>();
	ArrayList<Rectangle> bombRectangles = new ArrayList<Rectangle>();
	Texture bomb;
	int bombCount;

	private Music music;

	@Override
	public void create () {
		batch = new SpriteBatch();
		background = new Texture("bg.jpg");

		music = Gdx.audio.newMusic(Gdx.files.internal("coinmanmusic.mp3"));
		music.setLooping(true);
		music.setVolume(0.1f);
		music.play();

		man = new Texture[4];
		man[0] = new Texture("frame-1.png");
		man[1] = new Texture("frame-2.png");
		man[2] = new Texture("frame-3.png");
		man[3] = new Texture("frame-4.png");

		dizzy = new Texture("dizzy-1.png");

		manY = Gdx.graphics.getHeight() /2 - man[manState].getHeight() /2;

		choco = new Texture("chocolate.png");
		coin = new Texture("coin.png");
		bomb = new Texture("bomb.png");
		random = new Random();

		font2 = new BitmapFont();
		font2.setColor(Color.YELLOW);
		font2.getData().setScale(5);

		font = new BitmapFont();
		font.setColor(Color.RED);
		font.getData().setScale(10);
	}

	public void makeCoin(){
		float height = random.nextFloat() * Gdx.graphics.getHeight();
		coinYs.add((int) height);
		coinXs.add(Gdx.graphics.getWidth());
	}

	public void makeChoco(){
		float height = random.nextFloat() * Gdx.graphics.getHeight();
		chocoYs.add((int) height);
		chocoXs.add(Gdx.graphics.getWidth());
	}

	public void makeBomb(){
		float height = random.nextFloat() * Gdx.graphics.getHeight();
		bombYs.add((int) height);
		bombXs.add(Gdx.graphics.getWidth());
	}

	@Override
	public void render () {
		batch.begin();
		batch.draw(background, x, y, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		//speed = Gdx.graphics.getDeltaTime();
		//x = x + 100*speed;

		if (gameState == 1) {
			// GAME IS LIVE

			// BOMBS
			if (bombCount < 250) {
				bombCount++;
			}else {
				bombCount = 0;
				makeBomb();
			}

			bombRectangles.clear();
			for (int i=0;i<bombXs.size();i++){
				batch.draw(bomb, bombXs.get(i), bombYs.get(i));
				bombXs.set(i, bombXs.get(i) - 8);
				bombRectangles.add(new Rectangle(bombXs.get(i), bombYs.get(i), bomb.getWidth(), bomb.getHeight()));
			}

			// COINS
			if (coinCount < 100) {
				coinCount++;
			}else {
				coinCount = 0;
				makeCoin();
			}

			coinRectangles.clear();
			for (int i=0;i<coinXs.size();i++){
				batch.draw(coin, coinXs.get(i), coinYs.get(i));
				coinXs.set(i, coinXs.get(i) - 4);
				coinRectangles.add(new Rectangle(coinXs.get(i), coinYs.get(i), coin.getWidth(), coin.getHeight()));
			}

			// CHOCO
			if (chocoCount < 100) {
				chocoCount++;
			}else {
				chocoCount = 0;
				makeChoco();
			}

			chocoRectangles.clear();
			for (int i=0;i<chocoXs.size();i++){
				batch.draw(choco, chocoXs.get(i), chocoYs.get(i));
				chocoXs.set(i, chocoXs.get(i) - 4);
				chocoRectangles.add(new Rectangle(chocoXs.get(i), chocoYs.get(i), choco.getWidth(), choco.getHeight()));
			}

			if (Gdx.input.justTouched()) {
				velocity = -10;
			}

			if (pause < 8){
				pause++;
			}else {
				pause = 0;
				if (manState < 3) {
					manState++;
				} else {
					manState = 0;
				}
			}

			velocity += gravity;
			manY -= velocity;

			if (manY <= 0) {
				manY = 0;
			}

		} else if (gameState == 0) {
			// WAITING TO START

			font2.draw(batch, "Tap to PLAY!", 300, 200);

			if (Gdx.input.justTouched()) {
				gameState = 1;
			}

		} else if (gameState == 2) {
			// GAME OVER

			font2.draw(batch, "Tap to PLAY AGAIN!", 300, 200);

			if (Gdx.input.justTouched()) {
				gameState = 1;
				manY = Gdx.graphics.getHeight() /2 - man[manState].getHeight() /2;
				score = 0;
				velocity = 0;

				coinCount = 0;
				coinXs.clear();
				coinYs.clear();
				coinRectangles.clear();

				chocoCount = 0;
				chocoXs.clear();
				chocoYs.clear();
				chocoRectangles.clear();

				bombCount = 0;
				bombXs.clear();
				bombYs.clear();
				bombRectangles.clear();
			}

		}

		if (gameState == 2) {
			batch.draw(dizzy, Gdx.graphics.getWidth() / 2 - man[manState].getWidth() / 2, manY);
		}else {
			batch.draw(man[manState], Gdx.graphics.getWidth() / 2 - man[manState].getWidth() / 2, manY);
		}

		manRectangle = new Rectangle(Gdx.graphics.getWidth() /2 - man[manState].getWidth() /2, manY, man[manState].getWidth(), man[manState].getHeight());

		for (int i=0;i<coinRectangles.size();i++) {
			if (Intersector.overlaps(manRectangle, coinRectangles.get(i))) {
				score++;

				coinRectangles.remove(i);
				coinXs.remove(i);
				coinYs.remove(i);
				break;
			}
		}

		for (int i=0;i<chocoRectangles.size();i++) {
			if (Intersector.overlaps(manRectangle, chocoRectangles.get(i))) {
				score = score + 2;

				chocoRectangles.remove(i);
				chocoXs.remove(i);
				chocoYs.remove(i);
				break;
			}
		}

		for (int i=0;i<bombRectangles.size();i++) {
			if (Intersector.overlaps(manRectangle, bombRectangles.get(i))) {

				gameState = 2;
			}
		}

		font.draw(batch, String.valueOf(score), 100, 200);

		batch.end();
	}

	@Override
	public void dispose () {
		batch.dispose();
		music.dispose();
	}
}

