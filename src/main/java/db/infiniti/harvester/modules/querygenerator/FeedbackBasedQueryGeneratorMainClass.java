package db.infiniti.harvester.modules.querygenerator;

import java.util.ArrayList;
import java.util.List;

public class FeedbackBasedQueryGeneratorMainClass {

		public static void main(String[] args){
			FeedbackBasedQueryGenerator feedbackQG = new FeedbackBasedQueryGenerator();
			feedbackQG.prepareQuerySelection(1, "hi. this is mohammad. I am a phd student at university of twente. University of twente is very big.");
			List<String> initialQuery = new ArrayList<String>();
			String query = feedbackQG.setNextQueryLeastFromLast(initialQuery);
			System.out.print(query);
			
		}
}
