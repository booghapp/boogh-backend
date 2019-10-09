package org.boogh.clientservice.mapper;

import org.boogh.clientservice.dto.HonkDTO;
import org.boogh.domain.Honk;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class HonkMapper {
    public HonkDTO honkToHonkDTO(Honk honk) {
        return new HonkDTO(honk);
    }

    public List<HonkDTO> honksToHonkDTOs(List<Honk> honks) {
        return honks.stream()
            .filter(Objects::nonNull)
            .map(this::honkToHonkDTO)
            .collect(Collectors.toList());
    }
}
