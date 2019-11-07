package pt.konnectkode.kafka.tests;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import pt.konnectkode.kafka.cdi.annotation.KafkaConfig;
import pt.konnectkode.kafka.beans.KafkaMessageListener;
import pt.konnectkode.kafka.config.KafkaConfigProperties;
import pt.konnectkode.kafka.config.KafkaPropertiesResolverImpl;
import pt.konnectkode.kafka.consumer.Delegation;
import pt.konnectkode.kafka.cdi.extension.KafkaExtension;

import javax.enterprise.inject.spi.Extension;

public interface JavaArchiveUtils {

    static JavaArchive createFrameworkDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
                .addPackage(KafkaConfig.class.getPackage())
                .addClass(KafkaConfigProperties.class)
                .addClass(KafkaPropertiesResolverImpl.class)
                .addPackages(true, Delegation.class.getPackage())
                .addClasses(KafkaExtension.class)
                .addPackage(KafkaMessageListener.class.getPackage())
                .addAsServiceProvider(Extension.class, KafkaExtension.class)
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    static JavaArchive createBasicConfigurationDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
                .addPackage(KafkaConfig.class.getPackage())
                .addPackage(KafkaConfigProperties.class.getPackage())
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
    }

}