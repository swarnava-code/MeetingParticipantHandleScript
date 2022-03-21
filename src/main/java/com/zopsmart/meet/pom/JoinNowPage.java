package com.zopsmart.meet.pom;

import org.openqa.selenium.By;

public class JoinNowPage {
    private By joinNowButton = By.xpath("//span[text()='Join now']");
    private By camMicBlockPopupBy = By.xpath("//div[@class='g3VIld vdySc Up8vH J9Nfi iWO5td']");
    private By camMicBlockPopupDismissBy = By.xpath("(//span[text()='Dismiss'])[2]");

    public By getJoinNowButton() {
        return joinNowButton;
    }

    public By getCamMicBlockPopupBy() {
        return camMicBlockPopupBy;
    }

    public By getCamMicBlockPopupDismissBy() {
        return camMicBlockPopupDismissBy;
    }
}
