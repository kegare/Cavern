package cavern.miningassist;

public interface IMiningAssistExecutor
{
	public MiningAssist getType();

	public void start();

	public int calc();
}