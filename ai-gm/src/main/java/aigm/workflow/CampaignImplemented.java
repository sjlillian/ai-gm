package aigm.workflow;

import java.util.List;

import aigm.gamestate.Clock;
import aigm.gamestate.campaign.Crew;
import io.temporal.workflow.Workflow;

public class CampaignImplemented implements CampaignWorkflow {

	@Override
	public void run(Crew crew) {
		Workflow.continueAsNew(crew);
	}

	@Override
	public void startScore() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'startScore'");
	}

	@Override
	public void adjustHeat() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'adjustHeat'");
	}

	@Override
	public void adjustCoin() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'adjustCoin'");
	}

	@Override
	public void adjustRep() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'adjustRep'");
	}

	@Override
	public void adjustWantedLevel() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'adjustWantedLevel'");
	}

	@Override
	public void addClaim() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'addClaim'");
	}

	@Override
	public void addCrewAsset() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'addCrewAsset'");
	}

	@Override
	public Crew getCrew() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'getCrew'");
	}

	@Override
	public List<Clock> getActiveClocks() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'getActiveClocks'");
	}

}
