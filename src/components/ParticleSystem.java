package components;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import coreEngine.*;
import enumerations.*;
import utility.*;

public class ParticleSystem extends GameObject {
	
	Random rand; // Random Number Generator
	
	private List<Particle> particles; // List for Particles
	private List<Particle> deleteList; // List for Particles to Delete
	
	public ParticleSystem(Vector2D position, String path, double ppm, int tileCountX, int tileCountY,
			int particleCount, double minSpeed, double maxSpeed, double addVelocityX, double addVelocityY, double minRotationSpeed, double maxRotationSpeed, double minLifeTime, double maxLifeTime, double initTime) {
		
		super(position, 0.0, new Vector2D(1.0, 1.0), path, ppm, true, tileCountX, tileCountY, false, 0.0, GameObjectType.ParticleSystem);
		
		rand = new Random();
		
		particles = new ArrayList<>();
		deleteList = new ArrayList<>();
		
		// Create Particles
		for (int i = 0; i < particleCount; i++) {
			// Rotation
			double rotation = rand.nextDouble() * 360.0;
			
			// Velocity
			double speed = UtilityFunctions.Lerp(minSpeed, maxSpeed, rand.nextDouble());
			double sr = Math.toRadians(rand.nextDouble() * 360.0);
			Vector2D velocity = new Vector2D(Math.cos(sr) * speed, Math.sin(sr) * speed);
			velocity.x += addVelocityX;
			velocity.y += addVelocityY;
			
			// Rotation Velocity
			double rotationVelocity = UtilityFunctions.Lerp(minRotationSpeed, maxRotationSpeed, rand.nextDouble());
			
			// Sprite Index
			int spriteIndex = rand.nextInt(GetTileCount());
			
			// Life Time
			double lifeTime = UtilityFunctions.Lerp(minLifeTime, maxLifeTime, rand.nextDouble());
			
			// Create Particle and add to List
			Particle pa = new Particle(new Vector2D(position.x, position.y), rotation, velocity, rotationVelocity, -9.81, spriteIndex, lifeTime, initTime);
			particles.add(pa);
		}
	}
	
	@Override
	public void Update(FrameData frameData, InputManager inputManager, MouseInputManager mouseInputManager, GameManager gameManager) {
		super.Update(frameData, inputManager, mouseInputManager, gameManager);
		
		// Update Particles
		for (int i = 0; i < particles.size(); i++) {
			Particle pa = particles.get(i);
			
			pa.Update(frameData, inputManager, mouseInputManager);
			
			if (particles.get(i).deleteMe) {
				AddParticleToDelete(pa);
			}
		}
		
		// Delete GameObjects on Delete List
		while (deleteList.size() > 0) {
			Particle pa = deleteList.remove(0);
			DeleteParticle(pa);
		}
		
		// Delete this Particle System when no more Particles are in the List
		if (particles.size() == 0) {
			DeleteMe(gameManager);
		}
	}
	
	@Override
	public void DeleteMe(GameManager gameManager) {
		gameManager.AddParticleSystemToDelete(this);
	}
	
	// Add a Particle to the Delete List
	private void AddParticleToDelete(Particle pa) {
		if (!deleteList.contains(pa)) {
			deleteList.add(pa);
		}
	}
	
	// Delete a Particle
	private void DeleteParticle(Particle pa) {
		particles.remove(pa);
		pa = null;
	}
	
	public List<Particle> GetParticles() {
		return particles;
	}
}
