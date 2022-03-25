package com.mygdx.game;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import java.util.Iterator;
import java.util.Random;

public class GameScreen implements Screen {
	final Drop game;
	SpriteBatch batch;
	Texture background;
	Texture playerImage;
	Array<Texture> objectClassTexture = new Array<>();
	Music music;
	OrthographicCamera camera;
	Rectangle player;
	Array<Raindrop> raindrops = new Array<>();
	long dropTime;
	int dinoCollected = 0;
	int clipartCollected = 0;
	int molniaCollected = 0;
    int heroesCollected = 0;
	long startTime;
	long finishTime;


	public GameScreen(final Drop kik) {
		this.game = kik;
		background = new Texture("pictures/background.png");
		playerImage = new Texture(Gdx.files.internal("pictures/ben10.png"));
		objectClassTexture.add(new Texture(Gdx.files.internal("pictures/dino.png")));
		objectClassTexture.add(new Texture(Gdx.files.internal("pictures/clipart.png")));
		objectClassTexture.add(new Texture(Gdx.files.internal("pictures/molnia.png")));
		music = Gdx.audio.newMusic(Gdx.files.internal("sounds/music.mp3"));
		music.setLooping(true);
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 480);
        spawnPlayer();
		spawnHeroes();
		startTime = System.currentTimeMillis();
	}

	@Override
	public void render (float delta) {
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		camera.update();
		game.batch.setProjectionMatrix(camera.combined);

		game.batch.begin();
		game.batch.draw(background, 0, 0);

        game.font.draw(game.batch, "Heroes collected: " + heroesCollected, 0, 470);
        game.font.draw(game.batch, "Dino collected: " + dinoCollected, 0, 450);
		game.font.draw(game.batch, "Clipart collected: " + clipartCollected, 0, 430);
		game.font.draw(game.batch, "Molnia collected: " + molniaCollected, 0, 410);
		game.batch.draw(playerImage, player.x, player.y);

		for(Raindrop raindrop: raindrops) {
			game.batch.draw(raindrop.texture, raindrop.x, raindrop.y);
		}

		game.batch.end();

		if(Gdx.input.isTouched()) {
			Vector3 touchPos = new Vector3();
			touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
			camera.unproject(touchPos);
			player.x = touchPos.x - 32;
		}

		if(Gdx.input.isKeyPressed(Input.Keys.LEFT)){
			player.x -= 500 * Gdx.graphics.getDeltaTime();}
		if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
			player.x += 500 * Gdx.graphics.getDeltaTime();}

		if(player.x < 0){
			player.x = 0;}
		if(player.x > 800 - player.width){
			player.x = 800 - player.width;}

		if(TimeUtils.nanoTime() - dropTime > 1000000000){
			spawnHeroes();}

		Iterator<Raindrop> iterator = raindrops.iterator();
		while(iterator.hasNext()) {
			Raindrop raindrop = iterator.next();
			raindrop.y -= 200 * Gdx.graphics.getDeltaTime();
			if(raindrop.y + 64 < 0){
				iterator.remove();
			}
			if(raindrop.overlaps(player)) {
				switch (raindrop.type){
					case "dino":
						dinoCollected++;
						heroesCollected++;
						break;
					case "clipart":
						clipartCollected++;
						heroesCollected++;
						break;
					case "molnia":
						molniaCollected++;
						heroesCollected++;
						break;
				}
				iterator.remove();
			}
		}
		finishTime = System.currentTimeMillis();
		if (finishTime - startTime > 63000){
			raindrops.clear();
			music.stop();
			objectClassTexture.clear();
			game.setScreen(new EndGameScreen(game, heroesCollected));
		}
	}
	
	@Override
	public void dispose () {
		playerImage.dispose();
		music.dispose();
		batch.dispose();
		background.dispose();
	}

	private void spawnHeroes() {
		Raindrop raindrop = new Raindrop();
		raindrop.x = MathUtils.random(0, 650);
		raindrop.y = 480;
		raindrop.width = 250;
		raindrop.height = 400;
		Random random = new Random();
		int randomObjectTexture = random.nextInt(3);
		switch (randomObjectTexture){
			case 0:
				raindrop.texture = objectClassTexture.get(0);
				raindrop.type = "dino";
				break;
			case 1:
				raindrop.texture = objectClassTexture.get(1);
				raindrop.type = "clipart";
				break;
			case 2:
				raindrop.texture = objectClassTexture.get(2);
				raindrop.type = "molnia";
				break;

		}
		raindrops.add(raindrop);
		dropTime = TimeUtils.nanoTime();
	}

    private void spawnPlayer() {
        player = new Rectangle();
        player.x = 340;
        player.y = 0;
        player.width = 90;
        player.height = 200;
    }

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void show() {
		music.play();
	}

	@Override
	public void hide() {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}
}
