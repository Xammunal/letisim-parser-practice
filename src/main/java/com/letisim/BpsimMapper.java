package com.letisim;

import com.letisim.dto.LetisimScenarioDto;
import org.bpsim.model.Scenario;

public interface BpsimMapper {
    LetisimScenarioDto mapToDto(Scenario bpsimScenario);
}
