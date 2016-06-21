package org.dataconservancy.cos.rdf.annotations;

import org.dataconservancy.cos.ont.support.OwlClasses;
import org.dataconservancy.cos.ont.support.OwlProperties;

/**
 * Created by esm on 6/21/16.
 */
@OwlIndividual(OwlClasses.OSF_BO)
public class TestModelClass {

    @IndividualUri
    @OwlProperty(OwlProperties.DCTERMS_IDENTIFIER)
    private String id;


}
