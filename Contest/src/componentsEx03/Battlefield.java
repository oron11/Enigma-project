package componentsEx03;

import decryption.DecryptionManager;

public class Battlefield {
    private String battlefieldName;
    private int aliesCount;
    private DecryptionManager.LevelDifficultyMission levelDifficultyMission;
    private int roundsCount;

    public Battlefield(enigma.generated.Battlefield battlefield) {
        this.battlefieldName = battlefield.getBattleName();
        this.aliesCount = battlefield.getAllies();
        this.levelDifficultyMission = DecryptionManager.LevelDifficultyMission.valueOf(battlefield.getLevel());
        this.roundsCount = battlefield.getRounds();
    }

    public String getBattlefieldName() {
        return battlefieldName;
    }

    public int getAliesCount() {
        return aliesCount;
    }

    public DecryptionManager.LevelDifficultyMission getLevelDifficultyMission() {
        return levelDifficultyMission;
    }

    public int getRoundsCount() {
        return roundsCount;
    }
}
