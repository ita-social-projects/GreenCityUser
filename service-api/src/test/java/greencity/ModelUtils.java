package greencity;

import javax.validation.ConstraintValidatorContext;
import java.net.MalformedURLException;
import java.net.URL;

public class ModelUtils {

    public static URL getUrl() throws MalformedURLException {
        return new URL(TestConst.SITE);
    }

    public static ConstraintValidatorContext.ConstraintViolationBuilder getConstraintViolationBuilder() {
        return new ConstraintValidatorContext.ConstraintViolationBuilder() {
            @Override
            public NodeBuilderDefinedContext addNode(String name) {
                return null;
            }

            @Override
            public NodeBuilderCustomizableContext addPropertyNode(String name) {
                return null;
            }

            @Override
            public LeafNodeBuilderCustomizableContext addBeanNode() {
                return null;
            }

            @Override
            public ContainerElementNodeBuilderCustomizableContext addContainerElementNode(String name,
                Class<?> containerType,
                Integer typeArgumentIndex) {
                return null;
            }

            @Override
            public NodeBuilderDefinedContext addParameterNode(int index) {
                return null;
            }

            @Override
            public ConstraintValidatorContext addConstraintViolation() {
                return null;
            }
        };
    }
}
