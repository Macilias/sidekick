package com.macilias.apps.service.crowdtangle;

import java.io.IOException;
import java.util.Optional;

import com.macilias.apps.model.Settings;
import org.junit.Assert;
import org.junit.Test;

public class ServiceTest {

    @Test
    public void testFollowerCountReturnsNumberGreaterThanOrEqualToZero() throws IOException {
        Service service = new Service(new Client(Settings.CROWD_TANGLE_API_TOKEN), Settings.CROWD_TANGLE_LIST_ID);

        Assert.assertTrue("no, its not right",service.followerCount(Optional.empty(), Optional.empty()) > 0);
    }

}
