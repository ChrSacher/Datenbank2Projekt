package db2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;



import javax.annotation.PostConstruct;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;

import static java.time.format.DateTimeFormatter.ofLocalizedTime;

@Component
public class InitializeDB
{

    private static final Logger log = LoggerFactory.getLogger(InitializeDB.class);

   

    @PostConstruct
    public void init()
    {

		log.debug(" >>> Db initialized");
	
		DateTimeFormatter germanFormatter = ofLocalizedTime(FormatStyle.MEDIUM).withLocale(Locale.GERMAN);
		String s = LocalTime.now().minusMinutes(10).format(germanFormatter);


    }
}
