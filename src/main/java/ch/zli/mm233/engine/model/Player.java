package ch.zli.mm233.engine.model;

public record Player(
        int id,
        String name,
        Role role,
        Party partyCard,
        boolean alive
) {}
