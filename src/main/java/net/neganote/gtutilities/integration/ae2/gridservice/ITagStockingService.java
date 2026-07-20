package net.neganote.gtutilities.integration.ae2.gridservice;

import appeng.api.networking.IGridService;

public interface ITagStockingService extends IGridService {

    void markForRefresh(ITagStockingPart part);
}
