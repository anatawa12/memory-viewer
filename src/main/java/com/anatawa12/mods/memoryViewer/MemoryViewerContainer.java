package com.anatawa12.mods.memoryViewer;

import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.common.ModMetadata;

import java.util.Collections;

public class MemoryViewerContainer extends DummyModContainer {
    public MemoryViewerContainer() {
        super(new ModMetadata());
        ModMetadata meta = getMetadata();
        meta.modId = "memory-viewer";
        meta.name = "Memory Viewer";
        meta.version = "1.0.0";
        meta.authorList = Collections.singletonList("anatawa12");
        meta.description = "adds a new gui and show memory usage";
        meta.screenshots = new String[0];
        meta.url = "https://github.com/anatawa12/memory-viewer";
        meta.logoFile = "";
    }
}

