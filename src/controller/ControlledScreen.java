package controller;

import service.Service;

public interface ControlledScreen {
    public void reset_view();
    public void setScreenParent(ScreenController screenController);
    public void setService(Service service);
}
