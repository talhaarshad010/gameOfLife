package backend;
import java.util.ArrayList;
import java.util.Random;

import windowInterface.MyInterface;

public class Simulator extends Thread {

	private MyInterface mjf;
	
	private final int COL_NUM = 100;
	private final int LINE_NUM = 100;
	private final int LIFE_TYPE_NUM = 4;
	//Conway Radius : 1
	private final int LIFE_AREA_RADIUS = 1;
	//Animal Neighborhood Radius : 5
	private final int ANIMAL_AREA_RADIUS = 2;
	private ArrayList<Integer> fieldSurviveValues;
	private ArrayList<Integer> fieldBirthValues;
	
	private ArrayList<Agent> agents;

	private int[][] field;
	
	private boolean stopFlag;
	private boolean pauseFlag;
	private boolean loopingBorder;
	private boolean clickActionFlag;
	private int loopDelay = 150;

	//TODO : add missing attribute(s)

	public Simulator(MyInterface mjfParam) {
		mjf = mjfParam;
		stopFlag = false;
		pauseFlag = false;
		loopingBorder = false;
		clickActionFlag = false;

		agents = new ArrayList<Agent>();
		fieldBirthValues = new ArrayList<Integer>();
		fieldSurviveValues = new ArrayList<Integer>();

		field = new int[LINE_NUM][COL_NUM]; // Initialize the field

		// Default rule: Survive always, birth never
		for (int i = 0; i < 9; i++) {
			fieldSurviveValues.add(i);
		}
	}

	public int getWidth() {
		return COL_NUM;
	}

	public int getHeight() {
		return LINE_NUM;
	}

	//Should probably stay as is
	public void run() {
		int stepCount = 0;
		while (!stopFlag) {
			stepCount++;
			makeStep();
			mjf.update(stepCount);
			try {
				Thread.sleep(loopDelay);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			while (pauseFlag && !stopFlag) {
				try {
					Thread.sleep(loopDelay);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	/**
	 * method called at each step of the simulation
	 * makes all the actions to go from one step to the other
	 */
	public void makeStep() {
		// agent behaviors first
		// only modify if sure of what you do
		// to modify agent behavior, see liveTurn method
		// in agent classes
		for (int i = 0; i < agents.size(); i++) {
			Agent agent = agents.get(i);
			ArrayList<Agent> neighbors = this.getNeighboringAnimals(agent.getX(), agent.getY(), ANIMAL_AREA_RADIUS);
			if (!agent.liveTurn(neighbors, this)) {
				agents.remove(agent);
				i--; // Adjust the index since we removed an element
			}
		}
		// then evolution of the field
		int[][] newField = new int[LINE_NUM][COL_NUM];

		for (int y = 0; y < LINE_NUM; y++) {
			for (int x = 0; x < COL_NUM; x++) {
				int aliveNeighbors = countAliveNeighbors(x, y);
				if (field[y][x] == 1) {
					if (fieldSurviveValues.contains(aliveNeighbors)) {
						newField[y][x] = 1;
					} else {
						newField[y][x] = 0;
					}
				} else {
					if (fieldBirthValues.contains(aliveNeighbors)) {
						newField[y][x] = 1;
					} else {
						newField[y][x] = 0;
					}
				}
			}
		}
		field = newField;
	}




	private int countAliveNeighbors(int x, int y) {
		int count = 0;
		for (int i = -LIFE_AREA_RADIUS; i <= LIFE_AREA_RADIUS; i++) {
			for (int j = -LIFE_AREA_RADIUS; j <= LIFE_AREA_RADIUS; j++) {
				if (i == 0 && j == 0) {
					continue;
				}
				int neighborX = x + i;
				int neighborY = y + j;

				if (loopingBorder) {
					neighborX = (neighborX + COL_NUM) % COL_NUM;
					neighborY = (neighborY + LINE_NUM) % LINE_NUM;
				} else {
					if (neighborX < 0 || neighborY < 0 || neighborX >= COL_NUM || neighborY >= LINE_NUM) {
						continue;
					}
				}

				if (field[neighborY][neighborX] == 1) {
					count++;
				}
			}
		}
		return count;
	}


	/*
	 * leave this as is
	 */
	public void stopSimu() {
		stopFlag=true;
	}
	
	/*
	 * method called when clicking pause button
	 */
	public void togglePause() {
		// TODO : actually toggle the corresponding flag
		pauseFlag = !pauseFlag;
	}
	
	/**
	 * method called when clicking on a cell in the interface
	 */
	public void clickCell(int x, int y) {
		//TODO : complete method
		if (clickActionFlag) {
			// Add/remove an agent
			// Placeholder: assume we toggle an agent at this position
			for (Agent agent : agents) {
				if (agent.getX() == x && agent.getY() == y) {
					agents.remove(agent);
					return;
				}
			}
			agents.add(new Sheep(x,y));
		} else {
			// Toggle cell state
			field[y][x] = (field[y][x] == 0) ? 1 : 0;
		}
	}
	
	/**
	 * get cell value in simulated world
	 * @param x coordinate of cell
	 * @param y coordinate of cell
	 * @return value of cell
	 */
	public int getCell(int x, int y) {
		//TODO : complete method with proper return
		return field[y][x];
	}
	/**
	 * 
	 * @return list of Animals in simulated world
	 */
	public ArrayList<Agent> getAnimals(){
		return agents;
	}
	/**
	 * selects Animals in a circular area of simulated world
	 * @param x center
	 * @param y center
	 * @param radius
	 * @return list of agents in area
	 */
	public ArrayList<Agent> getNeighboringAnimals(int x, int y, int radius){
		ArrayList<Agent> inArea = new ArrayList<Agent>();
		for(int i=0;i<agents.size();i++) {
			Agent agent = agents.get(i);
			if(agent.isInArea(x,y,radius)) {
				inArea.add(agent);
			}
		}
		return inArea;
	}

	/**
	 * set value of cell
	 * @param x coord of cell
	 * @param y coord of cell
	 * @param val to set in cell
	 */
	public void setCell(int x, int y, int val) {
		//TODO : complete method
		field[y][x] = val;
	}
	
	/**
	 * 
	 * @return lines of file representing 
	 * the simulated world in its present state
	 */
	public ArrayList<String> getSaveState() {
		//TODO : complete method with proper return
		ArrayList<String> saveState = new ArrayList<String>();
		for (int y = 0; y < LINE_NUM; y++) {
			StringBuilder line = new StringBuilder();
			for (int x = 0; x < COL_NUM; x++) {
				line.append(field[y][x]).append(";");
			}
			saveState.add(line.toString());
		}
		return saveState;
	}
	/**
	 * 
	 * @param lines of file representing saved world state
	 */
	public void loadSaveState(ArrayList<String> lines) {
		/*
		 * First some checks that the file is usable
		 * We call early returns in conditions like this
		 * "Guard clauses", as they guard the method
		 * against unwanted inputs
		 */
//		if(lines.size()<=0) {
//			return;
//		}
//		String firstLine = lines.get(0);
//		String[] firstLineElements = firstLine.split(";");
//		if(firstLineElements.length<=0) {
//			return;
//		}
//		/*
//		 * now we fill in the world
//		 * with the content of the file
//		 */
//		for(int y =0; y<lines.size();y++) {
//			String line = lines.get(y);
//			String[] lineElements = line.split(";");
//			for(int x=0; x<lineElements.length;x++) {
//				String elem = lineElements[x];
//				int value = Integer.parseInt(elem);
//				setCell(x, y, value);
//			}
//		}
		if (lines.size() <= 0) {
			return;
		}
		for (int y = 0; y < lines.size(); y++) {
			String line = lines.get(y);
			String[] lineElements = line.split(";");
			for (int x = 0; x < lineElements.length; x++) {
				int value = Integer.parseInt(lineElements[x]);
				setCell(x, y, value);
			}
		}
	}

	/**
	 * called by button, with slider providing the argument
	 * makes a new world state with random cell states
	 * @param chanceOfLife the chance for each cell 
	 * to be alive in new state
	 */
	public void generateRandom(float chanceOfLife) {
		//TODO : complete method
		/*
		 * Advice :
		 * as you should probably have a separate class
		 * representing the field of cells...
		 * maybe just make a constructor in there 
		 * and use it here
		 */
		Random random = new Random();
		for (int y = 0; y < LINE_NUM; y++) {
			for (int x = 0; x < COL_NUM; x++) {
				field[y][x] = random.nextFloat() < chanceOfLife ? 1 : 0;
			}
		}
	}
	
	public boolean isLoopingBorder() {
		//TODO : complete method with proper return
		return loopingBorder;
	}
	
	public void toggleLoopingBorder() {
		//TODO : complete method
		loopingBorder = !loopingBorder;
	}
	
	public void setLoopDelay(int delay) {
		//TODO : complete method
		loopDelay = delay;
	}
	
	public void toggleClickAction() {
		//TODO : complete method
		clickActionFlag = !clickActionFlag;
	}

	/**
	 * prepare the content of a file saving present ruleSet
	 *  as you might want to save a state,
	 *  initialy written in this class constructor
	 *  as a file for future use
	 * @return File content as an ArrayList of Lines (String)
//	 * @see loadRule for inverse process
	 */
	public ArrayList<String> getRule() {
		//TODO : complete method with proper return
		ArrayList<String> rule = new ArrayList<String>();
		StringBuilder surviveLine = new StringBuilder();
		for (Integer value : fieldSurviveValues) {
			surviveLine.append(value).append(";");
		}
		StringBuilder birthLine = new StringBuilder();
		for (Integer value : fieldBirthValues) {
			birthLine.append(value).append(";");
		}
		rule.add(surviveLine.toString());
		rule.add(birthLine.toString());
		return rule;
	}

	public void loadRule(ArrayList<String> lines) {
		if(lines.size()<=0) {
			System.out.println("empty rule file");
			return;
		}
		//TODO : remove previous rule (=emptying lists)

		fieldSurviveValues.clear();
		fieldBirthValues.clear();
		String[] surviveElements = lines.get(0).split(";");
		for(int x=0; x<surviveElements.length;x++) {
			String elem = surviveElements[x];
			int value = Integer.parseInt(elem);
			//TODO : add value to possible survive values
			fieldSurviveValues.add(value);

		}
		String[] birthElements = lines.get(1).split(";");
		for(int x=0; x<birthElements.length;x++) {
			String elem = birthElements[x];
			int value = Integer.parseInt(elem);
			//TODO : add value to possible birth values
			fieldBirthValues.add(value);

		}
	}
	
	public ArrayList<String> getAgentsSave() {
		//TODO : Same idea as the other save method, but for agents
		ArrayList<String> agentsSave = new ArrayList<String>();
		for (Agent agent : agents) {
			agentsSave.add(agent.save());
		}
		return agentsSave;
	}

	public void loadAgents(ArrayList<String> agentData) {
		synchronized (agents) {
			agents.clear(); // Clear existing agents
			for (String data : agentData) {
				Agent agent = createAgent(data);
				if (agent != null) {
					agents.add(agent);
				}
			}
		}
	}
	private Agent createAgent(String agentData) {
		String[] parts = agentData.split(",");
		if (parts.length < 1) {
			return null; // Invalid data, return null
		}
		String agentType = parts[0]; // Extract the agent type
		switch (agentType) {
			case "Sheep":
				return createSheep(parts); // Create Sheep agent

			default:
				System.out.println("Unknown agent type: " + agentType);
				return null; // Unknown agent type, return null
		}
	}
	private Sheep createSheep(String[] parts) {

		if (parts.length >= 3) {
			int x = Integer.parseInt(parts[1]);
			int y = Integer.parseInt(parts[2]);
			return new Sheep(x, y); // Create Sheep instance
		} else {
			System.out.println("Invalid Sheep data: " + String.join(",", parts));
			return null; // Invalid Sheep data, return null
		}
	}

	/**
	 * used by label in interface to show the active click action
	 * @return String representation of click action
	 */
	public String clickActionName() {
		// TODO : initially return "sheep" or "cell"
		// depending on clickActionFlag
		return clickActionFlag ? "agent" : "cell";
	}

}
