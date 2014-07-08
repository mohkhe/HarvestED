package db.infiniti.harvester.modules.querygenerator;

public interface QueryGeneratorInterface {
	
	void generateQueries();
	String nextQuery();
	void resetForNextCollection();
}
