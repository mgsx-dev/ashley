package com.badlogic.ashley.core;

import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.utils.Array;

public class Benchmark {

	public static int entityProcessCount = 0;
	
	public static final int TOTAL_ENTITIES = 1000;
	public static final int TOTAL_COMPONENTS = 12;
	public static final int TOTAL_SYSTEMS = 100;
	public static final int TOTAL_ITERATIONS = 60;
	
	
	public static class BenchSystem extends EntitySystem{
		private Family family;
		private ImmutableArray<Entity> entities;
		private Array<ComponentMapper> mapper = new Array<ComponentMapper>();
		public BenchSystem() {
			super();
		}
		@Override
		public void addedToEngine(Engine engine) {
			super.addedToEngine(engine);
			entities = engine.getEntitiesFor(family);
		}

		public void setFamily(Family family) {
			this.family = family;
		}
		
		public void setComponentTypes(Array<Class<? extends Component>> types){
			setFamily(Family.all((Class[])types.toArray(Class.class)).get());
			for(Class<? extends Component> type : types){
				mapper.add(ComponentMapper.getFor(type));
			}
		}
		
		@Override
		public void update(float deltaTime) {
			for(Entity entity : entities){
				updateEntity(entity, deltaTime);
			}
		}
		private void updateEntity(Entity entity, float deltaTime) {
			for(int i=0 ; i<mapper.size ; i++){
				Component component = mapper.get(i).get(entity);
				// should be user processing
				// Math.random();
				entityProcessCount++;
			}
		}
	}
	
	public static void main(String[] args) throws Exception {
		
		Array<Class<? extends Component>> componentClasses = new Array<Class<? extends Component>>();
		
		ComponentClassFactory ccf = new ComponentClassFactory();
		for(int i=0 ; i<TOTAL_COMPONENTS ; i++){
			componentClasses.add(ccf.createComponentType("Compo" + i));
		}
		
		PooledEngine engine = new PooledEngine();
		
		SystemClassFactory scf = new SystemClassFactory();
		for(int i=0 ; i<TOTAL_SYSTEMS ; i++){
			Class<? extends BenchSystem> systemClass = scf.createSystemType("System" + i, BenchSystem.class);
			BenchSystem system = systemClass.newInstance();
			system.setComponentTypes(componentClasses);
			engine.addSystem(system);
		}
		
		for(int i=0 ; i<TOTAL_ENTITIES ; i++){
			Entity entity = engine.createEntity();
			for(Class<? extends Component> cType : componentClasses){
				Component comp = engine.createComponent(cType);
				entity.add(comp);
			}
			engine.addEntity(entity);
		}
		
		long TOTAL = TOTAL_ITERATIONS * TOTAL_SYSTEMS * TOTAL_COMPONENTS;
		
		for(int j=0 ; j<10 ; j++){
			
			entityProcessCount = 0;
			
			long before = System.currentTimeMillis();
			for(int i=0 ; i<TOTAL_ITERATIONS ; i++){
				engine.update(1f/60f);
				//Thread.sleep(1);
			}
			long after = System.currentTimeMillis();
			double time = (after - before) / 1000.0;
			System.out.println(entityProcessCount);
			System.out.println(time);
			
			entityProcessCount = 0;
			before = System.currentTimeMillis();
			for(long i=0 ; i<TOTAL ; i++){
				for(long k=0 ; k<TOTAL_ENTITIES ; k++){
					// Math.random();
					entityProcessCount++;
				}
				// float f = (float)Math.sqrt(2);
			}
			after = System.currentTimeMillis();
			time = (after - before) / 1000.0;
			System.out.println(entityProcessCount);
			System.out.println(time);
			
		}
		
	}
}
