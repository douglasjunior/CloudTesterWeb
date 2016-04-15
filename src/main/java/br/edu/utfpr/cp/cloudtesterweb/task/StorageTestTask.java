package br.edu.utfpr.cp.cloudtesterweb.task;

import java.io.Serializable;
import java.util.Date;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

/**
 *
 * @author Douglas
 */
@Startup
@Singleton
public class StorageTestTask implements Serializable {

    @Inject
    private StorageTestRunner runner;

    @Schedule(second = "*/5", minute = "*", hour = "*", persistent = false)
    public void task() {
        System.out.println("StorageTestTask: " + new Date());
        runner.run();
    }

}
