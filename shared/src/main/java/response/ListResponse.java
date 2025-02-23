package response;

import model.GameData;

import java.util.Collection;

public record ListResponse(Collection<GameData> games) {
}
